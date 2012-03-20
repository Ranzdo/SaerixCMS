package uicontrollers;

import com.saerix.cms.controller.Controller
import com.saerix.cms.database.*;

class Test extends Controller {
	def index() {
		showView("test", [content : "hohohoh"]);
		Database.getTable("")
	}
}
