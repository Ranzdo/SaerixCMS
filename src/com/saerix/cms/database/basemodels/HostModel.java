package com.saerix.cms.database.basemodels;

import java.sql.SQLException;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "hosts", rowclass = HostModel.HostRow.class)
public class HostModel extends Model {
	public static class HostRow extends Row {
		public int getKey() {
			return (Integer) getValue("host_key");
		}
		
		public int getId() {
			return (Integer) getValue("host_id");
		}
		
		public String getValue() {
			return (String) getValue("host_value");
		}
	}
	
	public Row getHost(String hostValue) throws SQLException {
		where("host_value", hostValue);
		return get().getRow();
	}
}
