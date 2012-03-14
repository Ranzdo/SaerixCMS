package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Table {
	Database database;
	private String tableName;

	public Table(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return database.getPrefix()+tableName;
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException {
		return database.con.prepareStatement(query);
	}
	
	public Connection getConnection() {
		return database.con;
	}
}
