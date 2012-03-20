package com.saerix.cms.database.basemodels;

import java.sql.SQLException;
import java.util.List;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
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
	
	@SuppressWarnings("unchecked")
	public List<ControllerRow> getAllControllers() throws SQLException {
		return (List<ControllerRow>) get().getRows();
	}
}
