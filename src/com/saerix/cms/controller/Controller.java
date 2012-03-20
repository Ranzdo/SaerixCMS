package com.saerix.cms.controller;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.ControllerModel.ControllerRow;
import com.saerix.cms.view.View;

public class Controller {
	private static Map<Integer, Class<? extends Controller>> controllers = Collections.synchronizedMap(new HashMap<Integer, Class<? extends Controller>>());
	
	public static Controller invokeController(int controllerId, String methodName, ControllerParameter parameters) throws Exception {
		if(methodName == null)
			throw new NullPointerException("The field methodName can not be null.");
		
		if(parameters == null)
			throw new NullPointerException("The field parameters can not be null.");
		
		Class<? extends Controller> clazz = null;
		synchronized(controllers) {
			clazz = controllers.get(controllerId);
		}
		if(clazz == null)
			throw new IllegalArgumentException("The controller with id "+controllerId+" was not found.");
		
		Controller controller = clazz.newInstance();
		controller.controllerParameter = parameters;
		Method method = clazz.getMethod(methodName);
		method.invoke(controller);
		return controller;
	}
	
	public static void reloadController(int controllerId) throws SQLException {
		ControllerRow row = (ControllerRow) Database.getTable("controllers").getRow(controllerId);
		if(row == null) {
			Class<? extends Controller> controller;
			synchronized (controllers) {
				controller = controllers.remove(controllerId);
			}
			if(controller == null) {
				throw new IllegalArgumentException("Could not find a controller with id "+controllerId);
			}
			else
				return;
		}
		
		Class<? extends Controller> controller = row.loadControllerClass(true);
		synchronized (controllers) {
			controllers.put(controllerId, controller);
		}
	}
	
	public static void reloadAllControllers() throws SQLException {
		controllers.clear();
		List<ControllerRow> rows = ((ControllerModel) Database.getTable("controllers")).getAllControllers();
		for(ControllerRow row : rows) {
			Class<? extends Controller> controller = row.loadControllerClass(true);
			synchronized (controllers) {
				controllers.put(row.getId(), controller);
			}
		}
	}
	
	private ControllerParameter controllerParameter;
	private ArrayList<View> views = new ArrayList<View>();
	ControllerRow databaseRow;
	
	
	public Controller() {
		
	}
	
	protected void showView(String viewName, Map<String, Object> variables) throws SQLException {
		View view = View.getView(viewName);
		if(view != null) {
			view.setController(this);
			view.setVariables(variables);
			views.add(view);
		}
		else
			throw new IllegalArgumentException("Could not find a view "+viewName);
	}
	
	public List<View> getViews() {
		return views;
	}
	
	public String getHost() {
		return controllerParameter.getHost();
	}
	
	public String getSegement(int place) {
		try {
			return controllerParameter.getSegments()[place];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String getPost(String parameter) {
		return controllerParameter.getPostParameters().get(parameter);
	}
	
	public String getGet(String parameter) {
		return controllerParameter.getGetParameters().get(parameter);
	}
}
