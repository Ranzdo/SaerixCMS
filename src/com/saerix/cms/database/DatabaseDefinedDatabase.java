package com.saerix.cms.database;

import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.ModelModel.ModelRow;

public class DatabaseDefinedDatabase extends Database {
	private int databaseId;
	private DatabaseLoader databaseLoader;
	
	DatabaseDefinedDatabase(DatabaseLoader databaseLoader, int databaseId, Properties properties) {
		super(properties);
		this.databaseId = databaseId;
		this.databaseLoader = databaseLoader;
	}
	
	public void reSyncWithDatabase() throws DatabaseException {
		models.clear();
		ModelModel model = getModelModel();
		for(ModelRow row : model.getModels(databaseId)) {
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
	
	public ModelRow addDatabaseModel(String tableName, String content) throws DatabaseException {
		ModelModel model = getModelModel();
		if(getModelModel().getModel(databaseId, tableName) != null)
			throw new DatabaseException("Model attatched to the table "+tableName+" to the database "+databaseId+" already exist.");
		
		model.addModel(databaseId, tableName, content);
		
		LoadedModel lmodel = parseClass(tableName, content);
		registerModel(lmodel);
		
		return (ModelRow) model.getRow(model.addModel(databaseId, tableName, content));
	}
	
	public ModelRow saveDatabaseModel(String tableName, String content) throws DatabaseException {
		ModelModel model = getModelModel();
		ModelRow current = model.getModel(databaseId, tableName);
		if(current == null)
			throw new DatabaseException("There is no model that is associated with the table "+tableName);
		
		LoadedModel lmodel = parseClass(current.getTableName(), content);
		
		registerModel(lmodel);
		
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("model_content", content);
		model.update(current.getId(), values);
		
		return (ModelRow) model.getRow(current.getId());
	}
	
	public void removeDatabaseModel(String tableName) throws DatabaseException {
		ModelModel model = getModelModel();
		ModelRow current = (ModelRow) model.getModel(databaseId, tableName);
		if(current == null)
			return;
		
		unRegisterModel(current.getTableName());
		
		model.remove(current.getId());
	}
	
	public int getDatabaseId() {
		return databaseId;
	}
}
