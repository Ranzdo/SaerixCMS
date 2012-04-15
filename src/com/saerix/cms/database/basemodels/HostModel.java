package com.saerix.cms.database.basemodels;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;

@Table(name = "hosts", rowclass = HostModel.HostRow.class)
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
	
	public Row getHost(String hostValue) throws DatabaseException {
		where("host_value", hostValue);
		return get().getRow();
	}
}
