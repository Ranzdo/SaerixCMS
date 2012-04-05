package com.saerix.cms.database;

import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.UserModel;
import com.saerix.cms.database.basemodels.ViewModel;

public class ModelLoader {
	private static Class<?>[] baseModels = {
		ModelModel.class,
		ViewModel.class,
		RouteModel.class,
		ControllerModel.class,
		UserModel.class,
		HostModel.class
	};
	
	private HashMap<String, Database> databases = new HashMap<String, Database>();
	private HashMap<Database, HashMap<String, LoadedModel>> models = new HashMap<Database, HashMap<String, LoadedModel>>();
	
	@SuppressWarnings("unchecked")
	public ModelLoader(Properties properties) throws DatabaseException {
		registerDatabase("main", properties);
		for(Class<?> model : baseModels) {
			if(!model.isAnnotationPresent(TableConfig.class))
				throw new DatabaseException("TableConfig annonation missing for "+model.getName());
		
			TableConfig config = model.getAnnotation(TableConfig.class);
			
			registerModel(new LoadedModel(config.name(), (Class<? extends Model>) model, config.rowclass()));
		}	
	}
	
	public void registerDatabase(String name, Properties properties) throws DatabaseException {
		if(databases.containsKey(name))
			throw new DatabaseException("Database with the name "+name+" already exists.");
		
		Database db = new Database(properties);
		
		databases.put(name, db);
		models.put(db, new HashMap<String, LoadedModel>());
	}
	
	public void registerModel(LoadedModel model) throws DatabaseException {
		Database database = databases.get(model.getDatabaseName());
		if(database == null)
			throw new DatabaseException("The database named "+model.getDatabaseName()+" could not be found when registering the model for the table "+model.getTableName());
		
		models.get(database).put(model.getTableName(), model);
	}
	
	public void unRegisterModel(String databaseName, String tableName) {
		Database database = databases.get(databaseName);
		if(database != null) {
			models.get(database).remove(tableName);
		}
	}
	
	public void unRegisterDatabase(String databaseName) {
		Database database = databases.remove(databaseName);
		if(database != null)
			models.remove(database);
	}
	
	public Model loadModel(String databaseName, String tableName) throws DatabaseException {
		Database database = databases.get(databaseName);
		if(database == null)
			throw new ModelNotFound("The database named "+databaseName+" could not be found when loading the model for the table "+tableName);
		
		LoadedModel lmodel = models.get(database).get(tableName);
		
		if(lmodel == null)
			throw new ModelNotFound("A model could not be found for the table "+tableName);
		
		return lmodel.generateModel(database);
	}
}
