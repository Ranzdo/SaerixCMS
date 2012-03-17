package models;

import com.saerix.cms.database.*;

@TableConfig(name = "users", rowclass = UserRow.class)
class UserTable extends Model {
	static class UserRow extends Row {
		def getUsername() {
			return getValue("username")
		}
	}
	
	def getUser(name) {
		where("username", name)
		return get().getRow()
	}
	
	def updateUsername(name, newname) {
		where("username", name)
		return update([username : newname])
	}
	
	def removeUser(name) {
		where("username", name)
		return remove()
	}
}
