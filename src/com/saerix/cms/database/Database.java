package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class Database {
	
	private Properties properties;
	private HashMap<Thread, Connection> connections = new HashMap<Thread, Connection>();
	HashMap<String, LoadedModel> models = new HashMap<String, LoadedModel>();
	
	public Database(Properties properties) {
		this.properties = properties;
	}
	
	public Connection getConnection() throws SQLException {
		Connection connection = connections.get(Thread.currentThread());
		if(connection != null)
			return connection;
		
		String connectionURL = "jdbc:mysql://"+properties.getProperty("mysql_hostname")+":"+properties.getProperty("mysql_port")+"/"+properties.getProperty("mysql_database");
		Properties connProperties = new java.util.Properties();
        connProperties.put("user", properties.getProperty("mysql_username"));
        connProperties.put("password", properties.getProperty("mysql_password"));
        
        connection = DriverManager.getConnection(connectionURL, connProperties);
        connections.put(Thread.currentThread(), connection);
		
		return connection;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String getTablePrefix() {
		return properties.getProperty("mysql_prefix");
	}
	
	public void registerModel(LoadedModel model) {
		models.put(model.getTableName(), model);
	}
	
	public void unRegisterModel(String tableName) {
		models.remove(tableName);
	}
	
	public Model getModel(String tableName) throws DatabaseException {
		LoadedModel model = models.get(tableName);
		if(model == null)
			throw new ModelNotFound(tableName);
		else
			return model.generateModel(this);
	}
}
