package com.saerix.cms.view;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.controller.Controller;

import groovy.lang.Script;

/** Methods that will be available in the view
 * @author Ranzdo
 *
 */
public abstract class ViewBase extends Script {
	
	public String view_controller(String controllerName, String methodName, Map<String, Object> passedVars) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Controller parentController = (Controller) getProperty("controller");
		Class<? extends Controller> controllerClass = Controller.getController(parentController.getControllerParameter().getHost().getId(), controllerName);
		Controller controller = Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter());
		
		controller.setPassedVariables(passedVars);
		
		return View.mergeViews(Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter()).getViews());
	}
	
	public String view_view(String viewName, Map<String, Object> map) throws SQLException {
		Controller parentController = (Controller) getProperty("controller");
		View view = View.getView(parentController.getControllerParameter().getHost().getId(), viewName);
		view.setVariables(map);
		view.setController(parentController);
		return view.evaluate();
	}
	
	public String base_url() {
		return SaerixCMS.getProperties().getProperty("base_url");
	}
}
