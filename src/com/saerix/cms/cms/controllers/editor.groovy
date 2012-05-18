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
		def hostid = getHost().getParentHost().getHostId();
		
		String type = get("type");
		String id = get("id", null);
		Model m = getModelFromType(type)
		if(m == null || id == null) {
			show_404()
			return;
		}
		
		def res;
		if(type == "controller") {
			res = m.getController(hostid, id).getRow().getContent();
		}
		else if(type == "model") {
			res = m.getModel(get("extra"), id).getRow().getContent();
		}
		else if(type == "view") {
			res = m.getView(hostid, id).getRow().getContent();
		}
		
		echo(res)
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
			echo(m.getDatabases().xml(["database_id", "database_name", "database_models", "model_tablename"] as Set))
		}
		else
			show_404()
	}
	
	void save() {
		String type = post("type")
		Model m = getModelFromType(type)
		if(m == null || post("name", null) == null) {
			show_404()
			return
		}
		String content = post("content")
		if(type == "controller") {
			try {
				getHost().getParentHost().saveController(post("name"), content)
			}
			catch(Exception e) {
				echo(Util.getStackTrace(e))
			}
		}
		else if(type == "model") {
			try {
				def databaseName = post("extra", null)
				def tableName = post("name", null)
				if(databaseName == null || tableName == null) {
					show_404()
					return
				}
				
				def database = getHost().getServer().getInstance().getDatabaseLoader().getDatabase(databaseName)
				
				if(database == null ? true : !(database instanceof DatabaseDefinedDatabase)) {
					show_404()
					return;
				}
				
				((DatabaseDefinedDatabase) database).saveDatabaseModel(tableName, post("content"))
				
			}
			catch(Exception e) {
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
				echo(getHost().getParentHost().addController(deafults.get(type).replace("%name", name)));
			}
			catch(Exception e) {
				echo("error:"+e.getMessage())
			}
		}
		else if(type == 'view') {
			try {
				getHost().getParentHost().addView(name, '');
			}
			catch(Exception e) {
				echo("error:"+e.getMessage())
			}
		}
		else if(type == 'database') {
			
		}
		else if(type == 'model') {
			
		}
	}
	
	void delete() {
		def type = get("type", null);
		def name = get("name", null);
		
		if(type == null || name == null) {
			show_404()
			return
		}
		
		if(type == 'controller') {
			getHost().getParentHost().deleteController(name);
		}
		else if(type == 'view') {
			getHost().getParentHost().deleteView(name);
		}
		else if(type == 'database') {
			
		}
		else if(type == 'model') {
			
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
		else
			return null
	}
}
