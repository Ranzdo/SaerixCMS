package com.saerix.cms.database.basemodels;

import java.util.HashMap;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;

@Table(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
public class ControllerModel extends Model {
	public static class ControllerRow extends Row {	
		public int getId() {
			return (Integer) getValue("controller_id");
		}
		
		public int getHostId() {
			return (Integer) getValue("host_id");
		}
		
		public String getName() {
			return (String) getValue("controller_name");
		}
		
		public String getContent() {
			return (String) getValue("controller_content");
		}
	}
	
	public Result getControllers() throws DatabaseException {
		return get();
	}
	
	public Result getControllers(int hostId) throws DatabaseException {
		where("host_id", hostId);
		return get();
	}
	
	public ControllerRow getController(int hostId, String controllerName) throws DatabaseException  {
		where("host_id", hostId);
		where("controller_name", controllerName);
		return (ControllerRow) get().getRow();
	}
	
	public Object addController(int hostId, String name, String content) throws DatabaseException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("host_id", hostId);
		values.put("controller_name", name);
		values.put("controller_content", content);
		return insert(values);
	}
}
