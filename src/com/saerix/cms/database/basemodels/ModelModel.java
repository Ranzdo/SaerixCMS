package com.saerix.cms.database.basemodels;

import java.sql.SQLException;
import java.util.List;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "models", rowclass = ModelModel.ModelRow.class)
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
	public List<ModelRow> getAllModels() throws SQLException {
		return (List<ModelRow>) get().getRows();
	}
}
