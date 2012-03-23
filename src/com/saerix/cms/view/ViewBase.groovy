package com.saerix.cms.view

import com.saerix.cms.*;
import com.saerix.cms.database.*
import com.saerix.cms.controller.*
import com.saerix.cms.views.*

abstract class ViewBase extends Script {
	def view_controller(String controllerName, String methodName, Map<String, Object> passedVars) {
		Controller parentController = getProperty("controller")
		def controllerClass = Controller.getController(parentController.getControllerParameter().getHostId(), controllerName)
		Controller controller = Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter())
		
		controller.setPassedVariables(passedVars)
		
		return View.mergeViews(Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter()).getViews())
	}
	
	def view(String viewName, Map<String, Object> map) {
		Controller parentController = getProperty("controller")
		
		View view = View.getView(parentController.getControllerParameter().getHostId(), viewName)
		view.setVariables(map)
		view.setController(parentController)
		
		return view.evaluate()
	}
	
	def view(String viewName) {
		return view(viewName, null);
	}
	
	def base_url() {
		Controller parentController = getProperty("controller")
		return parentController.base_url();
	}
	
	def anchor(String text, String segments, Map<String, String> getParameters) {
		return anchor(text, segments+writeGetParameters(getParameters))
	}
	
	def anchor(String text, String segments) {
		return "<a href=\""+base_url()+segments+"\">"+text+"</a>";
	}
	
	def writeGetParameters(Map<String, String> getParameters) {
		String returnThis = "?"
		for ( e in getParameters) {
			returnThis += URLEncoder.encode(e.key, "UTF-8")+"="+URLEncoder.encode(e.value, "UTF-8")+"&"
		}
		
		return returnThis.substring(0, returnThis.length()-2)
	}
}
