package com.saerix.cms.database.mainmodels;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;
import com.saerix.cms.database.XML;


@Table(name = "databases", rowclass = DatabaseModel.DatabaseRow.class)
public class DatabaseModel extends Model {
	public static class DatabaseRow extends Row {
		public DatabaseRow(DatabaseModel model, Map<String, Object> values) {
			super(model, values);
		}
		
		public String getName() {
			return (String) get("database_name");
		}
		
		public Properties getProperties() {
			Properties prop = new Properties();
			prop.put("database_url", get("database_url"));
			prop.put("database_username", get("database_username"));
			prop.put("database_password", get("database_password"));
			return prop;
		}
		
		@XML(rowname = "database_models")
		public Result getModels() throws DatabaseException {
			return ((ModelModel)model("models")).getModels(getName());
		}
	}
	
	public Result getDatabases() throws DatabaseException {
		return get();
	}
	
	public Result getDatabase(String name) throws DatabaseException {
		where("database_name", name);
		return get();
	}
	
	public Object addDatabase(String name, Properties properties) throws DatabaseException {
		Map<String, Object> insert = new LinkedHashMap<String, Object>();
		insert.put("database_name", name);
		insert.put("database_url", properties.get("database_url"));
		insert.put("database_username", properties.get("database_username"));
		insert.put("database_password", properties.get("database_password"));
		return insert(insert);
	}
}
