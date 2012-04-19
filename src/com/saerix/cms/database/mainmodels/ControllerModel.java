package com.saerix.cms.database.mainmodels;

import java.util.LinkedHashMap;
import java.util.Map;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
public class ControllerModel extends Model {
	public static class ControllerRow extends Row {	
		public int getId() {
			return (Integer) get("controller_id");
		}
		
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
	
	public ControllerRow getController(int hostId, String controllerName) throws DatabaseException  {
		where("host_id", hostId);
		where("controller_name", controllerName);
		return (ControllerRow) get().getRow();
	}
	
	public Object addController(int hostId, String name, String content) throws DatabaseException {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("host_id", hostId);
		values.put("controller_name", name);
		values.put("controller_content", content);
		return insert(values);
	}
}
