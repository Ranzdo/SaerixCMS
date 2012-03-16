package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.saerix.cms.SaerixCMS;

@TableConfig(name = "models", persistent = true, rowclass = ModelTable.ModelRow.class)
public class ModelTable extends Model {
	private static final String COLUMN_MODEL_ID = "model_id";
	private static final String COLUMN_MODEL_CONTENT = "model_content";
	
	private HashMap<Integer, Class<? extends Model>> cache = new HashMap<Integer, Class<? extends Model>>();
	
	public class ModelRow extends Row {
		public ModelRow(ResultSet set) throws SQLException {
			super(set);
		}
		
		@SuppressWarnings("unchecked")
		public Class<? extends Model> getModelClass() {
			Class<? extends Model> clazz = cache.get(getValue(COLUMN_MODEL_ID));
			if(clazz == null) {
				Class<?> clazz2 = SaerixCMS.getGroovyClassLoader().parseClass((String)getValue(COLUMN_MODEL_CONTENT));
				if(clazz2.getSuperclass() != Model.class)
					throw new InvalidSuperClass(clazz2.getSuperclass(), Model.class, clazz2);
				
				clazz = (Class<? extends Model>) clazz2;
				cache.put((Integer) getValue(COLUMN_MODEL_ID), clazz);
			}
			return clazz;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelRow> getAllModels() {
		try{
			return (List<ModelRow>) getAllRows();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
