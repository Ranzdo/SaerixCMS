package com.saerix.cms.database;

import java.sql.SQLException;
import java.util.List;

import com.saerix.cms.SaerixCMS;

@TableConfig(name = "models", rowclass = ModelTable.ModelRow.class)
public class ModelTable extends Model {
	@SuppressWarnings("unused")
	private static final String COLUMN_MODEL_ID = "model_id";
	private static final String COLUMN_MODEL_NAME = "model_name";
	private static final String COLUMN_MODEL_CONTENT = "model_content";
	
	public static class ModelRow extends Row {
		@SuppressWarnings("unchecked")
		public Class<? extends Model> loadModelClass(boolean reload) {
			if(!reload) {
				try {
					return (Class<? extends Model>) SaerixCMS.getGroovyClassLoader().loadClass("models."+(String)getValue(COLUMN_MODEL_NAME));
				}
				catch(ClassNotFoundException e) {
					return reload();
				}
			}
			else
				return reload();
		}
		
		public Class<? extends Model> loadModelClass() {
			return loadModelClass(false);
		}
		
		@SuppressWarnings("unchecked")
		private Class<? extends Model> reload() {
			Class<?> clazz = SaerixCMS.getGroovyClassLoader().parseClass((String)getValue(COLUMN_MODEL_CONTENT));
			if(clazz.getSuperclass() != Model.class)
				throw new InvalidSuperClass(clazz.getSuperclass(), Model.class, clazz);
			
			return (Class<? extends Model>) clazz;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ModelRow> getAllModels() {
		try {
			return (List<ModelRow>) get().getRows();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
