package com.saerix.cms.view

import com.saerix.cms.*;
import com.saerix.cms.database.*
import com.saerix.cms.controller.*
import com.saerix.cms.views.*

abstract class ViewBase extends Script {
	def view_controller(String controllerName, String methodName, Map<String, Object> passedVars) {
		def parentController = getProperty("controller")
		def controllerClass = Controller.getController(parentController.getControllerParameter().getHost().getId(), controllerName)
		def controller = Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter())
		
		controller.setPassedVariables(passedVars)
		
		return View.mergeViews(Controller.invokeController(controllerClass, methodName, parentController.getControllerParameter()).getViews())
	}
	
	def view_view(String viewName, Map<String, Object> map) {
		def parentController = getProperty("controller")
		
		View view = View.getView(parentController.getControllerParameter().getHost().getId(), viewName)
		view.setVariables(map)
		view.setController(parentController)
		
		return view.evaluate()
	}
	
	def base_url() {
		def parentController = getProperty("controller")
		return "http://"+parentController.getHostName()+"/"
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
			returnThis += URLDecoder.decode(e.key, "UTF-8")+"="+URLDecoder.decode(e.value, "UTF-8")+"&"
		}
		
		return returnThis.substring(0, returnThis.length()-2)
	}
}
