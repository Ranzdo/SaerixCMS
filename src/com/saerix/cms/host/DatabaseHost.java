package com.saerix.cms.host;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.controller.ControllerNotFoundException;
import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.ControllerModel.ControllerRow;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.RouteModel.RouteRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteType;
import com.saerix.cms.database.basemodels.ViewModel.ViewRow;
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
	
	public DatabaseHost(SaerixHttpServer server, int hostId, String hostName) {
		super(server, hostName);
		this.hostId = hostId;
		this.adminHost = new CMSHost(server, this);
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

	public Class<? extends Controller> reloadController(String controllerName) throws ControllerException, ControllerNotFoundException {
		try {
			ControllerRow row = ((ControllerModel)getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel("controllers")).getController(hostId, controllerName);
			
			if(row == null)
				throw new ControllerNotFoundException(controllerName);
			
			int controllerId = row.getId();
			
			Class<?> clazz = getServer().getInstance().getGroovyClassLoader().parseClass("package controllers"+controllerId+";"+row.getContent());
			
			if(!Controller.class.isAssignableFrom(clazz))
				throw new ControllerException("The controller "+controllerName+" loaded from database does not extend the Controller class.");
			
			@SuppressWarnings("unchecked")
			Class<? extends Controller> controller = (Class<? extends Controller>) clazz;
			
			loadedControllers.put(row.getName(), controller);
			
			return controller;
		}
		catch(DatabaseException e) {
			throw (ControllerException) new ControllerException().initCause(e);
		}
		catch(SQLException e) {
			throw (ControllerException) new ControllerException().initCause(e);
		}
	}

	public EvaluatedView reloadView(String viewName) throws ViewException, ViewNotFoundException {
		try {
			ViewRow row = ((ViewModel)getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel("views")).getView(hostId, viewName);
			
			if(row == null)
				throw new ViewNotFoundException(viewName);
			
			EvaluatedView eval = new EvaluatedView(getServer().getInstance().getGroovyClassLoader(), viewName, row.getContent());
			
			loadedViews.put(viewName, eval);
			
			return eval;
		}
		catch(SQLException e) {
			throw (ViewException) new ViewException().initCause(e);
		} catch (DatabaseException e) {
			throw (ViewException) new ViewException().initCause(e);
		}
	}

	@Override
	public Class<? extends Controller> getHostController(String controllerName) throws ControllerException {
		Class<? extends Controller> controller = loadedControllers.get(controllerName);
		if(controller == null)
			return reloadController(controllerName);
		
		return controller;
	}

	@Override
	public EvaluatedView getHostView(String viewName) throws ViewException {
		EvaluatedView eval = loadedViews.get(viewName);
		if(eval == null)
			return reloadView(viewName);
		
		return eval;
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
		catch(SQLException e) {
			throw (RouteException) new RouteException().initCause(e);
		} catch (DatabaseException e) {
			throw (RouteException) new RouteException().initCause(e);
		}
		
		return null;
	}
}
