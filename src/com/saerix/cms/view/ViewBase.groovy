package com.saerix.cms.view

import com.saerix.cms.*;
import com.saerix.cms.database.*
import com.saerix.cms.controller.*
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.views.*

abstract class ViewBase extends Script {
	def view_controller(String controllerName, String methodName, Map<String, Object> passedVars) {
		Controller parentController = getProperty("controller")
		def controllerClass = Controller.getController(parentController.getPageLoadEvent().getHostId(), controllerName)
		Controller controller = Controller.invokeController(controllerClass, methodName, parentController.getPageLoadEvent())
		
		controller.setPassedVariables(passedVars)
		
		return View.mergeViews(Controller.invokeController(controllerClass, methodName, parentController.getPageLoadEvent()).getViews())
	}
	
	def view(String viewName, Map<String, Object> vars) {
		Controller parentController = getProperty("controller")
		
		View view = View.getView(parentController.getPageLoadEvent().getHostId(), viewName)
		view.setVariables(vars)
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
	
	def anchor(String text, String segments, Map<String, String> parameters) {
		Controller parentController = getProperty("controller")
		return "<a href=\""+URLUtil.getURL(segments, parameters, parameters, parentController.getPageLoadEvent().isSecure())+"\">"+text+"</a>"
	}
	
	def anchor(String text, String segments) {
		return anchor(text, segments, null);
	}
}
