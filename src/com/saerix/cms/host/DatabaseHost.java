package com.saerix.cms.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.controller.ControllerNotFoundException;
import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.mainmodels.ControllerModel;
import com.saerix.cms.database.mainmodels.RouteModel;
import com.saerix.cms.database.mainmodels.ViewModel;
import com.saerix.cms.database.mainmodels.ControllerModel.ControllerRow;
import com.saerix.cms.database.mainmodels.RouteModel.RouteRow;
import com.saerix.cms.database.mainmodels.RouteModel.RouteType;
import com.saerix.cms.database.mainmodels.ViewModel.ViewRow;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.route.Route;
import com.saerix.cms.route.RouteException;
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.util.Util;
import com.saerix.cms.view.EvaluatedView;
import com.saerix.cms.view.ViewException;
import com.saerix.cms.view.ViewNotFoundException;

public class DatabaseHost extends Host {
	private int hostId;
	private CMSHost adminHost;
	
	private Map<String, Class<? extends Controller>> loadedControllers = Collections.synchronizedMap(new HashMap<String, Class<? extends Controller>>());
	private Map<String, EvaluatedView> loadedViews = Collections.synchronizedMap(new HashMap<String, EvaluatedView>());
	
	public DatabaseHost(SaerixHttpServer server, int hostId, String hostName) throws HostException {
		super(server, hostName);
		this.hostId = hostId;
		this.adminHost = new CMSHost(server, this);
		syncWithDatabase();
	}
	
	public int getHostId() {
		return hostId;
	}
	
	@Override
	public Route getRoute(PageLoadEvent pageLoadEvent) throws RouteException {
		String[] segmentArray = pageLoadEvent.getSegments();
		
		if(segmentArray.length >= 1 ? segmentArray[0].equalsIgnoreCase("admin") : false) {
			return adminHost.getRoute(createAdminPageLoadEvent(pageLoadEvent));
		}
		
		return super.getRoute(pageLoadEvent);
	}
	
	private PageLoadEvent createAdminPageLoadEvent(PageLoadEvent pageLoadEvent) {
		String[] segmentArray = pageLoadEvent.getSegments();
		String[] newArray;
		if(segmentArray.length-1 == 0) {
			newArray = new String[1];
			newArray[0] = "";
		}
		else {
			newArray = new String[segmentArray.length-1];
			for(int i = 1; i < segmentArray.length; i++)
				newArray[i-1] = segmentArray[i];
		}
		
		return new PageLoadEvent(adminHost, pageLoadEvent.isSecure(), newArray, pageLoadEvent.getGetParameters(), pageLoadEvent.getPostParameters(), pageLoadEvent.getCookies(), pageLoadEvent.getHandle());
	}
	
	@Override
	public void onPageLoad(PageLoadEvent pageLoadEvent) {
		String[] segmentArray = pageLoadEvent.getSegments();
		if(segmentArray.length >= 1 ? segmentArray[0].equalsIgnoreCase("admin") : false) {
			adminHost.onPageLoad(createAdminPageLoadEvent(pageLoadEvent));
		}
		super.onPageLoad(pageLoadEvent);
	}
	
	@Override
	public Collection<String> loadLibraries() {
		//TODO when lib is done
		return new ArrayList<String>();
	}

	@Override
	public Route getHostRoute(PageLoadEvent pageLoadEvent) throws RouteException {
		try {
			RouteModel routes = (RouteModel) getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel("routes");
			RouteRow row = routes.getRoute(hostId, Util.glue(pageLoadEvent.getSegments(), "/"));
			if(row != null) {
				if(row.getType() == RouteType.REDIRECT)
					return Route.get302Route(row.getRouteValue());
				
				String[] route = URLUtil.splitSegments(row.getRouteValue());
				
				if(route.length != 2)
					throw new RouteException("Invalid route for "+Util.glue(pageLoadEvent.getSegments(), "/")+" on row "+row);
				
				return new Route(this, route[0], route[1], pageLoadEvent);
			}
		}
		catch (DatabaseException e) {
			throw (RouteException) new RouteException().initCause(e);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void syncWithDatabase() throws HostException {
		try {
			/* Controllers */
			loadedControllers.clear();
			for(ControllerRow row : (List<ControllerRow>)getControllerModel().getControllers().getRows()) {
				loadedControllers.put(row.getName().toLowerCase(), evalController(row.getContent()));
			}
			
			/* Views */
			loadedViews.clear();
			for(ViewRow row : (List<ViewRow>)getViewModel().getViews().getRows()) {
				loadedViews.put(row.getName().toLowerCase(), evalView(row.getName(), row.getContent()));
			}
		}
		catch(Exception e) {
			throw (HostException) new HostException().initCause(e);
		}
	}
	
	/* Controller methods*/
	
	private ControllerModel getControllerModel() throws DatabaseException {
		return (ControllerModel)getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel("controllers");
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends Controller> evalController(String script) throws ControllerException, CompilationFailedException {
		Class<?> clazz = getServer().getInstance().getGroovyClassLoader().parseClass("package controllers;"+script);
		if(!Controller.class.isAssignableFrom(clazz))
			throw new ControllerException("The class does not extend the controller class.");
		
		return (Class<? extends Controller>) clazz;
	}
	
	public void addController(String script) throws ControllerException, CompilationFailedException, DatabaseException {
		Class<? extends Controller> controller = evalController(script);
		
		String name = controller.getSimpleName();
		
		ControllerModel model = getControllerModel();
		
		if(model.getController(hostId, name).length > 0)
			throw new ControllerException("It does already exist a controller named "+name+" on the host "+getHostName()); 
		
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("controller_content", script);
		
		model.addController(hostId, name, values);
		
		loadedControllers.put(name, controller);
	}
	
	public String saveController(String oldName, String script) throws ControllerException, CompilationFailedException, DatabaseException {
		Class<? extends Controller> controller = evalController(script);
		String newName = controller.getSimpleName();
		ControllerModel model = getControllerModel();
		
		ControllerRow old = (ControllerRow) model.getController(hostId, oldName).getRow();
		
		if(old == null)
			throw new ControllerException("The controller does not exist"); 
		
		if(!newName.equals(old.getName())) {
			if(model.getController(hostId, newName) != null)
				throw new ControllerException("It does already exist a controller named "+newName+" on the host "+getHostName());
			
			loadedControllers.remove(old.getName());
		}
		
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("controller_name", newName);
		values.put("controller_content", script);
		model.updateController(hostId, oldName, values);
		
		loadedControllers.put(newName, controller);
		
		return newName;
	}
	
	public void deleteController(String controllerName) throws ControllerException, DatabaseException {
		ControllerModel model = getControllerModel();
		
		model.removeController(hostId, controllerName);
		loadedControllers.remove(controllerName);
	}
	
	@Override
	public Class<? extends Controller> getHostController(String controllerName) throws ControllerException {
		Class<? extends Controller> controller = loadedControllers.get(controllerName);
		if(controller == null)
			throw new ControllerNotFoundException(controllerName);
		
		return controller;
	}
	
	/* View methods */
	
	private ViewModel getViewModel() throws DatabaseException {
		return (ViewModel)getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel("views");
	}
	
	private EvaluatedView evalView(String viewName, String content) throws ViewException {
		return new EvaluatedView(getServer().getInstance().getGroovyClassLoader(), viewName, content);
	}
	
	public void addView(String viewName, String content) throws ViewException, DatabaseException {
		ViewModel model = getViewModel();
		EvaluatedView view = evalView(viewName, content);
		
		if(model.getView(hostId, viewName).length > 0)
			throw new ViewException("A view with the name "+viewName+" already exist.");
		
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("view_content", content);
		model.addView(hostId, viewName, values);
		
		loadedViews.put(viewName, view);
	}
	
	public void saveView(String viewName, String content) throws ViewException, DatabaseException {
		ViewModel model = getViewModel();
		EvaluatedView view = evalView(viewName, content);
		
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("view_content", content);
		model.updateView(hostId, viewName, values);
		
		
		loadedViews.put(viewName, view);
	}
	
	public void deleteView(String viewName) throws DatabaseException {
		getViewModel().removeView(hostId, viewName);
		loadedViews.remove(viewName);
	}

	@Override
	public EvaluatedView getHostView(String viewName) throws ViewException {
		EvaluatedView eval = loadedViews.get(viewName);
		if(eval == null)
			throw new ViewNotFoundException(viewName);
		
		return eval;
	}
}
