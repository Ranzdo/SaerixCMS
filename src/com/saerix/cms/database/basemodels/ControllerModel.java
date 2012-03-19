package com.saerix.cms.database.basemodels;

import java.sql.SQLException;
import java.util.List;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.InvalidSuperClass;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
public class ControllerModel extends Model {
	public static class ControllerRow extends Row {
		@SuppressWarnings("unchecked")
		public Class<? extends Controller> loadControllerClass(boolean reload) {
			if(!reload) {
				try {
					return (Class<? extends Controller>) SaerixCMS.getGroovyClassLoader().loadClass("controllers."+(String)getValue("controller_name"));
				}
				catch(ClassNotFoundException e) {
					return reload();
				}
			}
			else
				return reload();
		}
		
		public Class<? extends Controller> loadControllerClass() {
			return loadControllerClass(false);
		}
		
		@SuppressWarnings("unchecked")
		private Class<? extends Controller> reload() {
			Class<?> clazz = SaerixCMS.getGroovyClassLoader().parseClass((String)getValue("controller_content"));
			if(clazz.getSuperclass() != Controller.class)
				throw new InvalidSuperClass(clazz.getSuperclass(), Controller.class, clazz);
			
			return (Class<? extends Controller>) clazz;
		}
		
		public int getId() {
			return (Integer) getValue("controller_id");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ControllerRow> getAllControllers() throws SQLException {
		return (List<ControllerRow>) get().getRows();
	}
}
