package com.saerix.cms.database;

public class LoadedModel {
	private final String tableName;
	private final Class<? extends Model> modelClass;
	private final Class<? extends Row> rowClass;
	private final String databaseName;
	
	public LoadedModel(String tableName, Class<? extends Model> modelClass, Class<? extends Row> rowClass, String databaseName) {
		this.tableName = tableName;
		this.modelClass = modelClass;
		this.rowClass = rowClass;
		this.databaseName = databaseName;
	}
	
	public LoadedModel(String tableName, Class<? extends Model> modelClass, Class<? extends Row> rowClass) {
		this.tableName = tableName;
		this.modelClass = modelClass;
		this.rowClass = rowClass;
		this.databaseName = "main";
	}
	
	public LoadedModel(String tableName, Class<? extends Model> modelClass) {
		this.tableName = tableName;
		this.modelClass = modelClass;
		this.rowClass = Row.class;
		this.databaseName = "main";
	}

	public String getTableName() {
		return tableName;
	}

	public Class<? extends Model> getModelClass() {
		return modelClass;
	}

	public Class<? extends Row> getRowClass() {
		return rowClass;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public Model generateModel(Database database) throws DatabaseException {
		try {
			Model model = modelClass.newInstance();
			model.database = database;
			model.loaded = this;
			try {
				model.setup();
			}
			catch(DatabaseException e) {
				throw e;
			}
			
			return model;
		} catch (Exception e) {
			throw (DatabaseException) new DatabaseException().initCause(e); 
		}
	}
}
