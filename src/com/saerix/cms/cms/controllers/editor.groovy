package com.saerix.cms.cms.controllers
import com.saerix.cms.controller.*
import com.saerix.cms.database.*
import com.saerix.cms.database.basemodels.*
import com.saerix.cms.view.*

import org.codehaus.groovy.control.CompilationFailedException
import com.saerix.cms.util.*

class editor extends Controller {
	private static deafults = [
		controller : "import com.saerix.cms.controller.Controller\n\nclass %name extends Controller {\n  boolean onload() {\n	return true;\n  }\n  def index() {\n	\n  }\n}",
		view : ""
	];
	
	boolean onload() {
		if(!session().userdata().getBoolean("loggedin")) {
			redirect("login")
			return false
		}
		else
			return true
	}
	
	void index() {
		view("editor", [
			controllers : model("controllers").getAllControllers(),
			models : model("models").getAllModels(),
			views : model("views").getAllViews(),
			post : post("test")
		])
	}
	
	void get() {
		String type = get("type");
		Model m = getModelFromType(type)
		if(m == null) {
			show_404()
			return;
		}
		
		def row = m.getRow(get("id"))
		
		if(row != null)
			echo(row.getContent())
		else
			show_404()
	}
	
	void save() {
		String type = post("type")
		Model m = getModelFromType(type)
		if(m == null || post("id", true) == null) {
			show_404()
			return
		}
		
		String content = post("content")
		if(type == "controller") {
			try {
				Class clazz = getHost().getServer().getInstance().getGroovyClassLoader().parseClass("package controllers;"+content)
				
				m.update(post("id", true), [controller_name : clazz.getSimpleName(), controller_content : content])
					
				getHost().getParentHost().reloadController(post("name"))
			}
			catch(CompilationFailedException e) {
				echo(Util.getStackTrace(e))
			}
		}
		else if(type == "model") {
			try {
				getHost().getServer().getInstance().getGroovyClassLoader().parseClass("package models;"+content)
				
				
				//TODO
				
				
				getHost().getServer().getInstance().getDatabaseLoader().getMainDatabase().reloadDatabaseModel(null)
			}
			catch(CompilationFailedException e) {
				echo(Util.getStackTrace(e))
			}
		}
		else if(type == "view") {
			try {
				new EvaluatedView(getHost().getServer().getInstance().getGroovyClassLoader(), post("name"), content)
				
				m.update(post("id", true), [view_content : content])
					
				getHost().getParentHost().reloadView(post("name"));
			}
			catch(ViewException e) {
				echo(Util.getStackTrace(e))
			}
		}
		
	}
	
	void newitem() {
		def type = get("type");
		def name = get("name");
		Model m = getModelFromType(get("type"))
		if(m == null || get("name", true) == null) {
			show_404()
			return
		}
		else if(name == "") {
			echo("error:Invalid name.")
			return;
		}
		
		m.where("host_id", getHost().getParentHost().getHostId())
		m.where(get("type")+"_name", name)
		
		if(m.get().length > 0) {
			echo("error:There is already a "+type+" with that name.")
			return;
		}
		
		echo(m.insert([
			host_id : getHost().getParentHost().getHostId(),
			(get("type")+"_name") : get("name"),
			(get("type")+"_content") : editor.deafults.get(type).replace("%name", name)
		]));
	}
	
	private getModelFromType(String type) {
		if(type == "controller") {
			return model("controllers")
		}
		else if(type == "model") {
			return model("models")
		}
		else if(type == "view") {
			return model("views")
		}
		else if(type == "library") {
			return model("libraries")
		}
		else
			return null
	}
}
