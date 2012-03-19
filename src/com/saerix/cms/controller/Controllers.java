package com.saerix.cms.controller;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ControllerModel.ControllerRow;
import com.saerix.cms.view.View;


public class Controllers {
	public static Map<Integer, Class<? extends Controller>> controllers = Collections.synchronizedMap(new HashMap<Integer, Class<? extends Controller>>());
	
	public static List<View> invokeController(int controllerId) {
		Class<? extends Controller> clazz;
		synchronized(controllers) {
			clazz = controllers.get(controllerId);
		}
		return null;
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
	
}
