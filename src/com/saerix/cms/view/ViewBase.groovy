package com.saerix.cms.view

import com.saerix.cms.*;
import com.saerix.cms.database.*
import com.saerix.cms.controller.*
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.views.*

abstract class ViewBase extends Script {
	def view(String viewName, Map<String, Object> vars) {
		Controller parentController = getProperty("controller")
		
		View view = parentController.getHost().getView(viewName);
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
	
	def url(String segments, Map<String, String> parameters) {
		Controller parentController = getProperty("controller")
		return URLUtil.getURL(parentController.getHostName(), segments, parameters, parentController.getPageLoadEvent().isSecure())
	}
	
	def url(String segments) {
		return url(segments, null)
	}
	
	def anchor(String text, String segments, Map<String, String> parameters) {
		return "<a href=\""+url(segments, parameters)+"\">"+text+"</a>"
	}
	
	def anchor(String text, String segments) {
		return anchor(text, segments, null);
	}
	
	def resource_url(String resourcename) {
		Controller parentController = getProperty("controller")
		return URLUtil.getURL(parentController.getHostName(), "res/"+resourcename, null, parentController.getPageLoadEvent().isSecure());
	}
}
