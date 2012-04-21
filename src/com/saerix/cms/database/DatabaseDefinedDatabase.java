package com.saerix.cms.database;

import java.util.Properties;
import java.util.List;

import com.saerix.cms.database.mainmodels.ModelModel;
import com.saerix.cms.database.mainmodels.ModelModel.ModelRow;

public class DatabaseDefinedDatabase extends Database {
	private int databaseId;
	private DatabaseLoader databaseLoader;
	
	DatabaseDefinedDatabase(DatabaseLoader databaseLoader, int databaseId, Properties properties) {
		super(properties);
		this.databaseId = databaseId;
		this.databaseLoader = databaseLoader;
	}
	
	@SuppressWarnings("unchecked")
	public void reSyncWithDatabase() throws DatabaseException {
		models.clear();
		ModelModel model = getModelModel();
		for(ModelRow row : (List<ModelRow>) model.getModels(databaseId).getRows()) {
			registerModel(parseClass(row.getTableName(), row.getContent()));
		}
	}
	
	@SuppressWarnings("unchecked")
	private LoadedModel parseClass(String tableName, String script) throws DatabaseException {
		Class<?> clazz = databaseLoader.getInstance().getGroovyClassLoader().parseClass("package models;"+script);
		if(!Model.class.isAssignableFrom(clazz))
			throw new DatabaseException("The model class \""+clazz.getName()+"\" does not extend the model class.");
		
		Class<? extends Row> rowClass = Row.class;
		
		if(clazz.isAnnotationPresent(RowClass.class))
			rowClass = clazz.getAnnotation(RowClass.class).value();
		
		return new LoadedModel(tableName, (Class<? extends Model>)clazz, rowClass);
	}
	
	private ModelModel getModelModel() throws DatabaseException {
		return (ModelModel) databaseLoader.getMainDatabase().getModel("models");
	}
	
	public void addDatabaseModel(String tableName, String content) throws DatabaseException {
		ModelModel model = getModelModel();
		LoadedModel lmodel = parseClass(tableName, content);
		if(model.getModel(databaseId, tableName) != null)
			throw new DatabaseException("Model attatched to the table "+tableName+" to the database "+databaseId+" already exist.");
		
		model.addModel(databaseId, tableName, content);
		
		registerModel(lmodel);
	}
	
	public void saveDatabaseModel(String tableName, String content) throws DatabaseException {
		ModelModel model = getModelModel();
		ModelRow current = (ModelRow) model.getModel(databaseId, tableName).getRow();
		if(current == null)
			throw new DatabaseException("There is no model that is associated with the table "+tableName);
		
		LoadedModel lmodel = parseClass(current.getTableName(), content);
		
		registerModel(lmodel);
		
		model.updateModel(databaseId, tableName, content);
	}
	
	public void removeDatabaseModel(String tableName) throws DatabaseException {
		ModelModel model = getModelModel();
		
		unRegisterModel(tableName);
		
		model.removeModel(databaseId, tableName);
	}
	
	public int getDatabaseId() {
		return databaseId;
	}
}
