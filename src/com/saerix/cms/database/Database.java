package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.ModelTable.ModelRow;

public final class Database {
	public static final String mysql_prefix = SaerixCMS.getProperties().getProperty("mysql_prefix");
	
	private static Class<?>[] baseModels = {
		ModelTable.class,
		TemplateTable.class
	};
	
	private static Map<Thread, Database> databaseConnections = Collections.synchronizedMap(new HashMap<Thread, Database>());
	
	public static synchronized Model getTable(String tableName) {
		Database database = databaseConnections.get(Thread.currentThread());
		if(database == null) {
			//TODO If it can't connect to database, what to do?
			try {
				database = new Database();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			databaseConnections.put(Thread.currentThread(), database);
			database.reloadModels();
		}
		return database.getModel(tableName);
	}
	
	
	private Map<String, Class<? extends Model>> mappedModels = new HashMap<String, Class<? extends Model>>();
	
	private Connection con;
	
	public Database() throws SQLException {
		Properties properties = SaerixCMS.getProperties();
		String connectionURL = "jdbc:mysql://"+properties.getProperty("mysql_hostname")+":"+properties.getProperty("mysql_port")+"/"+properties.getProperty("mysql_database");
		Properties connProperties = new java.util.Properties();
        connProperties.put("user", properties.getProperty("mysql_username"));
        connProperties.put("password", properties.getProperty("mysql_password"));
        connProperties.put("autoReconnect", "true");
        connProperties.put("maxReconnects", "3");
        con = DriverManager.getConnection(connectionURL, connProperties);
	}
	
	public Model getModel(String tableName) {
		Class<? extends Model> clazz = mappedModels.get(tableName);
		Model model = null;
		if(clazz != null) {
			try {
				model = clazz.newInstance();
			}		
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else
			model = new Model(tableName);
		model.database = this;
		model.setup();
		return model;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadModels() {
		mappedModels.clear();
		
		for(Class<?> clazz : baseModels)
			mappedModels.put(clazz.getAnnotation(TableConfig.class).name(), (Class<? extends Model>) clazz);
		
		for(ModelRow row : ((ModelTable)Database.getTable("models")).getAllModels()) {
			Class<? extends Model> clazz = row.loadModelClass();
			mappedModels.put(clazz.getAnnotation(TableConfig.class).name(), clazz);
		}
	}
	
	public Connection getConnection() {
		return con;
	}
}
