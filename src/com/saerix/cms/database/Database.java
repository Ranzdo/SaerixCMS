package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;

public final class Database {
	private static Database database;
	
	public static void initiate(String mysql_hostname, int mysql_port,
			String mysql_username, String mysql_password,
			String mysql_database, String mysql_prefix) throws SQLException {
		
		if(database == null)
			database = new Database(mysql_hostname, mysql_port, mysql_username, mysql_password, mysql_database, mysql_prefix);
	}
	
	public static Model getTable(String tableName) {
		return database._getTable(tableName);
	}
	
	Connection con;
	
	private HashMap<String, Model> tables = new HashMap<String, Model>();
	
	private String mysql_hostname;
	private int mysql_port;
	private String mysql_username;
	private String mysql_password;
	private String mysql_database;
	private String mysql_prefix;
	
	public Database(String mysql_hostname, int mysql_port,
			String mysql_username, String mysql_password,
			String mysql_database, String mysql_prefix) throws SQLException {
		this.mysql_hostname = mysql_hostname;
		this.mysql_port = mysql_port;
		this.mysql_username = mysql_username;
		this.mysql_password = mysql_password;
		this.mysql_database = mysql_database;
		this.mysql_prefix = mysql_prefix;
		
		connect();
	}
	
	private void connect() throws SQLException {
		String connectionURL = "jdbc:mysql://"+mysql_hostname+":"+mysql_port+"/"+mysql_database;
		Properties connProperties = new java.util.Properties();
        connProperties.put("user", mysql_username);
        connProperties.put("password", mysql_password);
        connProperties.put("autoReconnect", "true");
        connProperties.put("maxReconnects", "3");
        con = DriverManager.getConnection(connectionURL, connProperties);
	}
	
	public String getPrefix() {
		return mysql_prefix;
	}
	
	public Model _getTable(String tableName) {
		Model table = tables.get(tableName);
		if(table == null)
			throw new IllegalArgumentException("There is no table with the name "+tableName);
		return table;
	}
}
