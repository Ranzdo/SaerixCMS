package com.saerix.cms.database.mainmodels;

import java.util.HashMap;
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
		
		@XML(rowname = "database_models")
		public Result getModels() throws DatabaseException {
			return ((ModelModel)model("models")).getModels(getId());
		}
	}
	
	public Result getDatabases() throws DatabaseException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("database_id", -1);
		values.put("database_name", "main");
		DatabaseRow row = new DatabaseRow(this, values);
		Result result = get();
		result.addRow(row);
		return result;
	}
	
	public DatabaseRow getDatabase(String name) throws DatabaseException {
		where("database_name", name);
		return (DatabaseRow) get().getRow();
	}
	
	public Object addDatabase(String name, Properties properties) throws DatabaseException {
		HashMap<String, Object> insert = new HashMap<String, Object>();
		insert.put("database_name", name);
		insert.put("database_url", properties.get("database_url"));
		insert.put("database_username", properties.get("database_username"));
		insert.put("database_password", properties.get("database_password"));
		return insert(insert);
	}
}
