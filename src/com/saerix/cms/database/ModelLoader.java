package com.saerix.cms.database;

import groovy.lang.GroovyClassLoader;

import java.util.HashMap;
import java.util.Properties;

import org.codehaus.groovy.control.CompilationFailedException;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.ModelModel.ModelRow;
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

	private GroovyClassLoader classLoader;
	
	@SuppressWarnings("unchecked")
	public ModelLoader(GroovyClassLoader classLoader, Properties properties) throws DatabaseException {
		this.classLoader = classLoader;
		registerDatabase("main", properties);
		for(Class<?> model : baseModels) {
			if(!model.isAnnotationPresent(TableConfig.class))
				throw new DatabaseException("TableConfig annonation missing for "+model.getName());
		
			TableConfig config = model.getAnnotation(TableConfig.class);
			
			registerModel(new LoadedModel(config.name(), (Class<? extends Model>) model, config.rowclass()));
		}
		
	}
	
	public void reloadDatabaseModel(String databaseName, String tableName) {
		
	}
	
	private void reloadDatabaseModel(String databaseName, int databaseId, String tableName) throws CompilationFailedException, DatabaseException {
		ModelRow row = ((ModelModel)loadModel("main", "models")).getModel(databaseId, tableName);
		Class<?> clazz = classLoader.parseClass(row.getContent());
		if(!Model.class.isAssignableFrom(clazz))
			throw new DatabaseException("The model class \""+clazz.getName()+"\" does not extend the model class.");
		
		@SuppressWarnings("unchecked")
		Class<? extends Model> model = (Class<? extends Model>) clazz;
		
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
		if(database != null)
			models.get(database).remove(tableName);
	}
	
	public void unRegisterDatabase(String databaseName) {
		if(databaseName.equals("main"))
			throw new IllegalArgumentException("Cannot unregister the main database.");
		
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
