package com.saerix.cms.database.basemodels;

import java.util.List;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
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

	@SuppressWarnings("unchecked")
	public List<ModelRow> getAllModels() throws DatabaseException {
		return (List<ModelRow>) get().getRows();
	}
	
	public ModelRow getModel(int databaseId, String tableName) throws DatabaseException  {
		where("database_id", databaseId);
		where("model_tablename", tableName);
		return (ModelRow) get().getRow();
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelRow> getModels(int databaseId) throws DatabaseException {
		where("database_id", databaseId);
		return (List<ModelRow>) get().getRows();
	}
}
