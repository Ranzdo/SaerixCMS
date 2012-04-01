package com.saerix.cms.cms.controllers
import com.saerix.cms.controller.*;
import com.saerix.cms.database.*;
import com.saerix.cms.database.basemodels.*;
import com.saerix.cms.view.*;

class editor extends Controller {
	
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
			views : model("views").getAllViews()
		]);
	}
	
	void item() {
		String type = get("type");
		Model m = null;
		if(type == "controller") {
			m = model("controllers")
		}
		else if(type == "model") {
			m = model("models")
		}
		else if(type == "view") {
			m = model("views")
		}
		else if(type == "library") {
			m = model("libraries")
		}
		else {
			show_404()
			return;
		}
		
		def row = m.getRow(get("id"))
		
		if(row != null)
			echo(row.getContent())
		else
			show_404();
	}
	
	void save() {
		
	}
}
