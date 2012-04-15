package com.saerix.cms.database;

import groovy.lang.GroovyClassLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.UserModel;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.ModelModel.ModelRow;

public class MainDatabase extends Database {
	
	private static Class<?>[] baseModels = {
		ModelModel.class,
		ViewModel.class,
		RouteModel.class,
		ControllerModel.class,
		UserModel.class,
		HostModel.class
	};
	
	private GroovyClassLoader classLoader;

	@SuppressWarnings("unchecked")
	public MainDatabase(GroovyClassLoader classLoader, Properties properties) throws DatabaseException {
		super(properties);
		this.classLoader = classLoader;
		
		for(Class<?> model : baseModels) {
			if(!model.isAnnotationPresent(Table.class))
				throw new DatabaseException("TableConfig annonation missing for "+model.getName());
		
			Table config = model.getAnnotation(Table.class);
			
			registerModel(new LoadedModel(config.name(), (Class<? extends Model>) model, config.rowclass()));
		}
		
		try {
			List<ModelRow> rows = ((ModelModel)getModel("models")).getModels(-1);
			
			for(ModelRow row : rows) {
				reloadDatabaseModel(row);
			}
		} catch (SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
	
	public void reloadDatabaseModel(ModelRow row) throws DatabaseException {
		Class<?> clazz = classLoader.parseClass("package models;"+row.getContent());
		if(!Model.class.isAssignableFrom(clazz))
			throw new DatabaseException("The model class \""+clazz.getName()+"\" does not extend the model class.");
		
		@SuppressWarnings("unchecked")
		Class<? extends Model> model = (Class<? extends Model>) clazz;
		Class<? extends Row> rowclass = Row.class;
		if(model.isAnnotationPresent(RowClass.class))
			rowclass = model.getAnnotation(RowClass.class).value();
		
		registerModel(new LoadedModel(row.getTableName(), model, rowclass));
	}
}
