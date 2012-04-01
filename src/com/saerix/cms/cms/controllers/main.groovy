package com.saerix.cms.cms.controllers
import com.saerix.cms.controller.*;
import com.saerix.cms.database.*;
import com.saerix.cms.database.basemodels.*;
import com.saerix.cms.view.*;

class main extends Controller {
	def index() {
		if(!session().userdata().getBoolean("loggedin")) {
			redirect("login")
			return;
		}
	}
}