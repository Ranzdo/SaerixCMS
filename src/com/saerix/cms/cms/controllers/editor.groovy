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
		view("editor")
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
	
	void getall() {
		String type = get("type", null)
		Model m = getModelFromType(type)
		if(m == null) {
			show_404()
			return;
		}
		
		def hostid = getHost().getParentHost().getHostId();
		
		setMime("text/xml");
		if(type == "controller") {
			echo(m.getControllers(hostid).xml(["controller_id", "controller_name"] as Set))
		}
		else if(type == "view") {
			echo(m.getViews(hostid).xml(["view_id", "view_name"] as Set))
		}
		else if(type == "database") {
			echo(m.getDatabases().xml(["database_id", "database_name", "database_models", "model_tablename", "model_id"] as Set))
		}
		else
			show_404()
	}
	
	void save() {
		String type = post("type")
		Model m = getModelFromType(type)
		if(m == null || post("id", null) == null) {
			show_404()
			return
		}
		String content = post("content")
		if(type == "controller") {
			try {
				getHost().getParentHost().saveController(Integer.parseInt(post("id")), content).getName()
			}
			catch(Exception e) {
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
				getHost().getParentHost().saveView(post("name"), content);
			}
			catch(Exception e) {
				echo(Util.getStackTrace(e))
			}
		}
		
	}
	
	void newitem() {
		def type = get("type");
		def name = get("name");
		Model m = getModelFromType(get("type"))
		if(m == null || get("name", null) == null) {
			show_404()
			return
		}
		else if(name == "") {
			echo("error:Invalid name.")
			return;
		}
		
		if(type == 'controller') {
			try {
				echo(getHost().getParentHost().addController(deafults.get(type).replace("%name", name)).getId());
			}
			catch(Exception e) {
				echo("error:"+e.getMessage())
			}
		}
		else if(type == 'view') {
			try {
				echo(getHost().getParentHost().addView(name, '').getId());
			}
			catch(Exception e) {
				echo("error:"+e.getMessage())
			}
		}
	}
	
	private getModelFromType(String type) {
		if(type == "database") {
			return model("databases");
		}
		else if(type == "controller") {
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
