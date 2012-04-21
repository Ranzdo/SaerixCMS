package com.saerix.cms.database.mainmodels;

import java.util.Map;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
public class ControllerModel extends Model {
	public static class ControllerRow extends Row {		
		public int getHostId() {
			return (Integer) get("host_id");
		}
		
		public String getName() {
			return (String) get("controller_name");
		}
		
		public String getContent() {
			return (String) get("controller_content");
		}
	}
	
	public Result getControllers() throws DatabaseException {
		return get();
	}
	
	public Result getControllers(int hostId) throws DatabaseException {
		where("host_id", hostId);
		return get();
	}
	
	public Result getController(int hostId, String controllerName) throws DatabaseException  {
		where("host_id", hostId);
		where("controller_name", controllerName);
		return get();
	}
	
	public Object addController(int hostId, String name, Map<String, Object> values) throws DatabaseException {
		values.put("host_id", hostId);
		values.put("controller_name", name);
		return insert(values);
	}
	
	public void removeController(int hostId, String controllerName) throws DatabaseException {
		where("host_id", hostId);
		where("controller_name", controllerName);
		remove();
	}
	
	public Object updateController(int hostId, String controllerName, Map<String, Object> values) throws DatabaseException {
		where("host_id", hostId);
		where("controller_name", controllerName);
		return update(values);
	}
}
