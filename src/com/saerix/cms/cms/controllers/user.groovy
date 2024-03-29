package com.saerix.cms.cms.controllers
import com.saerix.cms.controller.*;
import com.saerix.cms.database.*;
import com.saerix.cms.database.basemodels.*;
import com.saerix.cms.view.*;
import com.saerix.cms.sessionlib.*;

class user extends Controller {
	def login() {
		if(session().userdata().getBoolean("loggedin"))
			redirect("")
		
		if(!post("submit", null) != null) {
			def user = model("users").verify(post("username"), post("password"))
			if(user != null) {
				session().userdata().set("loggedin", true)
				session().userdata().set("user", user)
				redirect("")
			}
			else
				view("login", [error : "Wrong username or password"])
		}
		else
			view("login", [error : null]);
	}
	
	def logout() {
		if(session().userdata().getBoolean("loggedin"))
			session().destroy();
		
		redirect("login")
	}
}