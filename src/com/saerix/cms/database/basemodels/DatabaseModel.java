package com.saerix.cms.database.basemodels;

import java.util.Properties;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;

@Table(name = "databases", rowclass = DatabaseModel.DatabaseRow.class)
public class DatabaseModel extends Model {
	public static class DatabaseRow extends Row {
		public int getId() {
			return (Integer) getValue("database_id");
		}
		
		public String getName() {
			return (String) getValue("database_name");
		}
		
		public Properties getProperties() {
			Properties prop = new Properties();
			prop.put("database_url", getValue("database_url"));
			prop.put("database_username", getValue("database_username"));
			prop.put("database_password", getValue("database_password"));
			return prop;
		}
	}
	
	public DatabaseRow getDatabase(String name) throws DatabaseException {
		where("database_name", name);
		return (DatabaseRow) get().getRow();
	}
}
