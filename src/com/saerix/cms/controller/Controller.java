package com.saerix.cms.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.InvalidSuperClass;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.ControllerModel.ControllerRow;
import com.saerix.cms.libapi.Library;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.sessionlib.Session;
import com.saerix.cms.sessionlib.SessionLibrary;
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.util.Util;
import com.saerix.cms.view.View;

public class Controller {
	private static Map<Integer, Class<? extends Controller>> controllersById = Collections.synchronizedMap(new HashMap<Integer, Class<? extends Controller>>());
	private static Map<String, Integer> controllersByName = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
	private static Map<File, Class<? extends Controller>> localControllers = Collections.synchronizedMap(new HashMap<File, Class<? extends Controller>>());

	@SuppressWarnings("unchecked")
	public static Class<? extends Controller> getLocalController(String filePath) throws IOException {
		File file = new File("cms"+File.separator+"controllers"+File.separator+filePath.replace("/", File.separator)+".groovy");
		if(!file.exists())
			return null;
		
		Class<? extends Controller> clazz = localControllers.get(file);
		
		if(clazz == null || SaerixCMS.getInstance().isInDevMode()) {
			clazz = SaerixCMS.getGroovyClassLoader().parseClass("package cmscontrollers;"+Util.readFile(file));
			if(clazz.getSuperclass() != Controller.class)
				throw new InvalidSuperClass(clazz.getSuperclass(), Controller.class, clazz);
			
			synchronized(localControllers) {
				localControllers.put(file, clazz);
			}
		}
		
		return (Class<? extends Controller>) clazz;
	}
	
	public static Class<? extends Controller> getController(int hostId, String controllerName) {
		Integer controllerId = controllersByName.get(hostId+":"+controllerName);
		
		if(controllerId == null)
			throw new IllegalArgumentException("The controller with the name "+controllerName+" was not found.");
		
		return getController(controllerId);
	}
	
	public static Class<? extends Controller> getController(int controllerId) {
		Class<? extends Controller> clazz = controllersById.get(controllerId);
		if(clazz == null)
			throw new IllegalArgumentException("The controller with id "+controllerId+" was not found.");
		return clazz;
	}
	
	public static Controller invokeController(Class<? extends Controller> controllerClass, String methodName, PageLoadEvent pageLoadEvent) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if(controllerClass == null)
			throw new NullPointerException("The field controllerClass can not be null.");
		
		if(methodName == null)
			throw new NullPointerException("The field methodName can not be null.");
		
		if(pageLoadEvent == null)
			throw new NullPointerException("The field parameters can not be null.");
		
		Controller controller = controllerClass.newInstance();
		controller.controllerParameter = pageLoadEvent;
		Method method = controllerClass.getMethod(methodName);
		method.invoke(controller);
		return controller;
	}
	
	public static void reloadController(ControllerRow controllerRow) throws SQLException {
		if(controllerRow == null)
			throw new NullPointerException("The field controllerRow can not be null.");
		
		int controllerId = controllerRow.getId();
		
		Class<?> clazz = SaerixCMS.getGroovyClassLoader().parseClass("package controllers"+controllerRow.getHostId()+";"+controllerRow.getContent());
		
		if(clazz.getSuperclass() != Controller.class)
			throw new InvalidSuperClass(clazz.getSuperclass(), Controller.class, clazz);
		
		@SuppressWarnings("unchecked")
		Class<? extends Controller> controller = (Class<? extends Controller>) clazz;
		
		synchronized(controllersByName) {
			String remove = null;
			for(Entry<String, Integer> entry : controllersByName.entrySet()) {
				if(entry.getValue() == controllerRow.getId()) {
					remove = entry.getKey();
					break;
				}
			}
			if(remove != null)
				controllersByName.remove(remove);
			
			controllersByName.put(controllerRow.getHostId()+":"+controllerRow.getName(), controllerId);
		}
		
		synchronized(controllersById) {
			controllersById.put(controllerId, controller);
		}
	}
	
	public static void reloadAllControllers() throws SQLException {
		controllersById.clear();
		List<ControllerRow> rows = ((ControllerModel) Database.getTable("controllers")).getAllControllers();
		for(ControllerRow row : rows) {
			reloadController(row);
		}
	}
	
	private String redirect = null;
	private PageLoadEvent controllerParameter;
	private Map<String, Object> passedVars = null;
	private ArrayList<View> views = new ArrayList<View>();
	
	public Controller() {
		
	}
	
	public void view(String viewName, Map<String, Object> variables) throws SQLException, IOException {
		View view = View.getView(controllerParameter.getHostId(), viewName);
		if(view != null) {
			view.setController(this);
			view.setVariables(variables);
			views.add(view);
		}
		else
			throw new IllegalArgumentException("Could not find a view "+viewName);
	}
	
	public void view(String viewName) throws SQLException, IOException {
		view(viewName, null);
	}
	
	public List<View> getViews() {
		return views;
	}
	
	public PageLoadEvent getPageLoadEvent() {
		return controllerParameter;
	}
	
	public String getHostName() {
		return controllerParameter.getHostName();
	}
	
	public String segment(int index) {
		try {
			return controllerParameter.getSegments()[index];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String post(String parameter) {
		List<String> list = controllerParameter.getPostParameters().get(parameter);
		
		return list == null ? "" : list.size() < 1 ? "" : list.get(0);
	}
	
	public String get(String parameter) {
		List<String> list = controllerParameter.getGetParameters().get(parameter);
		
		return list == null ? "" : list.size() < 1 ? "" : list.get(0);
	}
	
	public Model model(String tableName) {
		return Database.getTable(tableName);
	}
	
	public Object getPassedVariable(String variableName) {
		return passedVars == null ? null : passedVars.get(variableName);
	}
	
	public void setPassedVariables(Map<String, Object> vars) {
		passedVars = vars;
	}
	
	public void redirect(String segments, Map<String, String> para) {
		redirect = URLUtil.getURL(getHostName(), segments, para, controllerParameter.isSecure());
	}
	
	public void redirect(String segments) {
		redirect = URLUtil.getURL(getHostName(), segments, null, controllerParameter.isSecure());
	}
	
	public String willRedirect() {
		return redirect;
	}
	
	public String base_url() {
		return URLUtil.getURL(getHostName(), "", null, controllerParameter.isSecure());
	}
	
	public Library lib(String libName) {
		return SaerixCMS.getInstance().getLibraryLoader().getLib(libName);
	}
	
	public Session session() {
		return ((SessionLibrary)lib("session")).session();
	}
}
