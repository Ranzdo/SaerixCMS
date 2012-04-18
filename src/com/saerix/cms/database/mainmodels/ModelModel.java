package com.saerix.cms.database.mainmodels;

import java.util.HashMap;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "models", rowclass = ModelModel.ModelRow.class)
public class ModelModel extends Model {
	
	public static class ModelRow extends Row {
		public int getId() {
			return (Integer) getValue("model_id");
		}

		public int getDatabaseId() {
			return (Integer) getValue("database_id");
		}
		
		public String getTableName() {
			return (String) getValue("model_tablename");
		}
		
		public String getContent() {
			return (String) getValue("model_content");
		}
	}

	public Result getAllModels() throws DatabaseException {
		return get();
	}
	
	public ModelRow getModel(int databaseId, String tableName) throws DatabaseException  {
		where("database_id", databaseId);
		where("model_tablename", tableName);
		return (ModelRow) get().getRow();
	}
	
	public Result getModels(int databaseId) throws DatabaseException {
		where("database_id", databaseId);
		return get();
	}
	
	public Object addModel(int databaseId, String tableName, String content) throws DatabaseException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("database_id", databaseId);
		values.put("model_tablename", tableName);
		values.put("model_content", content);
		return insert(values);
	}
}
