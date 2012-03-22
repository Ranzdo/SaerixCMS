
import com.saerix.cms.controller.*;
class test extends Controller {
	def index() {
		showView("login", [content: "kekeke"])
	}
}