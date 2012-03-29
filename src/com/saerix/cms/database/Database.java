package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.UserModel;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.ModelModel.ModelRow;

public final class Database {
	public static final String mysql_prefix = SaerixCMS.getProperties().getProperty("mysql_prefix");
	
	private static Class<?>[] baseModels = {
		ModelModel.class,
		ViewModel.class,
		RouteModel.class,
		ControllerModel.class,
		UserModel.class,
		HostModel.class
	};
	
	private static Map<Thread, Database> databaseConnections = Collections.synchronizedMap(new HashMap<Thread, Database>());
	
	private static DatabaseCleaner cleaner = new DatabaseCleaner();
	
	public static Model getTable(String tableName) {
		if(cleaner.getState() == Thread.State.NEW)
			cleaner.start();
		
		Database database = databaseConnections.get(Thread.currentThread());
		
		try {
			if(database == null) {
				database = new Database();
				synchronized (databaseConnections) {
					databaseConnections.put(Thread.currentThread(), database);
				}
				database.reloadModels();
			}
			/*else if(!database.con.isValid(3)) {
				database = new Database();
				synchronized (databaseConnections) {
					databaseConnections.put(Thread.currentThread(), database);
				}
				database.reloadModels();
			}*/
		}
		catch(SQLException e) {
			//TODO If it can't connect to database, what to do?
			e.printStackTrace();
		}

		Model model = database.getModel(tableName);
		if(model == null)
			throw new IllegalArgumentException("An model to the table "+tableName+" was not found.");
		else
			return model;
	}
	
	public static void reloadAllModels() {
		synchronized (databaseConnections) {
			for(Entry<Thread, Database> entry : databaseConnections.entrySet()) {
				entry.getValue().reloadModels();
			}
		}
	}
	
	public static void reloadModel(ModelRow row) {
		synchronized (databaseConnections) {
			for(Entry<Thread, Database> entry : databaseConnections.entrySet()) {
				entry.getValue().reloadModel2(row);
			}
		}
	}
	
	//TODO Is reload of models thread-safe? probly not
	private Map<String, Class<? extends Model>> mappedModels = Collections.synchronizedMap(new HashMap<String, Class<? extends Model>>());
	
	private Connection con;
	
	private long lastActive = System.currentTimeMillis();
	
	public Database() throws SQLException {
		Properties properties = SaerixCMS.getProperties();
		String connectionURL = "jdbc:mysql://"+properties.getProperty("mysql_hostname")+":"+properties.getProperty("mysql_port")+"/"+properties.getProperty("mysql_database");
		Properties connProperties = new java.util.Properties();
        connProperties.put("user", properties.getProperty("mysql_username"));
        connProperties.put("password", properties.getProperty("mysql_password"));
        con = DriverManager.getConnection(connectionURL, connProperties);
	}
	
	public Model getModel(String tableName) {
		lastActive = System.currentTimeMillis();
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
			throw new TableNotFound(tableName);
		model.database = this;
		model.setup();
		return model;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadModels() {
		lastActive = System.currentTimeMillis();
		mappedModels.clear();
		
		for(Class<?> clazz : baseModels)
			mappedModels.put(clazz.getAnnotation(TableConfig.class).name(), (Class<? extends Model>) clazz);
		
		for(ModelRow row : ((ModelModel)Database.getTable("models")).getAllModels()) {
			reloadModel(row);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reloadModel2(ModelRow row) {
		Class<?> clazz = SaerixCMS.getGroovyClassLoader().parseClass("package models"+row.getHostId()+";"+row.getContent());
		if(!Model.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("The supplied class does not extend model.");
		
		if(!clazz.isAnnotationPresent(TableConfig.class))
			throw new IllegalArgumentException("Table config not set.");
		
		mappedModels.put(clazz.getAnnotation(TableConfig.class).name(), (Class<? extends Model>) clazz);
	}
	
	public Connection getConnection() {
		lastActive = System.currentTimeMillis();
		return con;
	}
	
	private static class DatabaseCleaner extends Thread {
		@Override
		public void run() {
			while(true) {
				ArrayList<Thread> toRemove = new ArrayList<Thread>();
				for(Entry<Thread, Database> entry : databaseConnections.entrySet()) {
					Database database = entry.getValue();
					if(System.currentTimeMillis() - database.lastActive > 60000) {
						toRemove.add(entry.getKey());
					}
				}
				for(Thread t : toRemove) {
					databaseConnections.remove(t);
				}
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
