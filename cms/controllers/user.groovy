import com.saerix.cms.controller.*;
import com.saerix.cms.database.*;
import com.saerix.cms.database.basemodels.*;
import com.saerix.cms.view.*;
import com.saerix.cms.session.*;

class user extends Controller {
	def login() {
		(SessionLibrary)lib("session")
	}
	
	def logout() {
			
	}
}
