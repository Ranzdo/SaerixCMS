package com.saerix.cms.database.mainmodels;

import java.util.LinkedHashMap;
import java.util.Map;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "models", rowclass = ModelModel.ModelRow.class)
public class ModelModel extends Model {
	
	public static class ModelRow extends Row {
		public int getDatabaseName() {
			return (Integer) get("database_name");
		}
		
		public String getTableName() {
			return (String) get("model_tablename");
		}
		
		public String getContent() {
			return (String) get("model_content");
		}
	}

	public Result getAllModels() throws DatabaseException {
		return get();
	}
	
	public Result getModel(String databaseName, String tableName) throws DatabaseException  {
		where("database_name", databaseName);
		where("model_tablename", tableName);
		return get();
	}
	
	public Result getModels(String databaseName) throws DatabaseException {
		where("database_name", databaseName);
		return get();
	}
	
	public Object addModel(String databaseName, String tableName, String content) throws DatabaseException {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("database_name", databaseName);
		values.put("model_tablename", tableName);
		values.put("model_content", content);
		return insert(values);
	}
	
	public void updateModel(String databaseName, String tableName, String content) throws DatabaseException {
		where("database_name", databaseName);
		where("model_tablename", tableName);
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("model_content", content);
		update(values);
	}
	
	public void removeModel(String databaseName, String tableName) throws DatabaseException {
		where("database_name", databaseName);
		where("model_tablename", tableName);
		remove();
	}
}
