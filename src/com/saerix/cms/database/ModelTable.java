package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.saerix.cms.SaerixCMS;

@TableConfig(name = "models", persistent = true, rowclass = ModelTable.ModelRow.class)
public class ModelTable extends Model {
	public HashMap<Integer, Class<? extends Model>> cache = new HashMap<Integer, Class<? extends Model>>();
	
	public class ModelRow extends Row {
		public ModelRow(ResultSet set) throws SQLException {
			super(set);
		}
		
		@SuppressWarnings("unchecked")
		public Class<? extends Model> getModelClass() {
			Class<? extends Model> clazz = cache.get(getValue("model_id"));
			if(clazz == null) {
				Class<?> clazz2 = SaerixCMS.getGroovyClassLoader().parseClass((String)getValue("model_content"));
				if(clazz2.getSuperclass() != Model.class)
					throw new InvalidSuperClass(clazz2.getSuperclass(), Model.class, clazz2);
				
				clazz = (Class<? extends Model>) clazz2;
				cache.put((Integer) getValue("model_id"), clazz);
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
