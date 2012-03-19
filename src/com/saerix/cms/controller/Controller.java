package com.saerix.cms.controller;

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
	
	public static Class<? extends Controller> getController(int controllerId) {
		Class<? extends Controller> clazz = null;
		synchronized(controllers) {
			clazz = controllers.get(controllerId);
		}
		return clazz;
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
	
	
	private ArrayList<View> views = new ArrayList<View>();
	ControllerRow databaseRow;
	
	
	public Controller() {
		
	}
	
	protected void showView(String viewName, Map<String, Object> variables) throws SQLException {
		View view = View.getView(viewName);
		if(view != null) {
			view.setVariables(variables);
			views.add(view);
		}
		else
			throw new IllegalArgumentException("Could not find a view "+viewName);
	}
	
	public List<View> getViews() {
		return views;
	}
}
