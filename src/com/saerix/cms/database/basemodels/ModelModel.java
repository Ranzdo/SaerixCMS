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

		public int getHostId() {
			return (Integer) getValue("host_id");
		}
		
		public String getName() {
			return (String) getValue("model_name");
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
