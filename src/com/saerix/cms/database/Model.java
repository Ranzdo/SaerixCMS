package com.saerix.cms.database;

import java.lang.reflect.Constructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Model {
	Database database;
	
	public String getTableName() {
		TableConfig tableConfig = getClass().getAnnotation(TableConfig.class);
		if(tableConfig != null)
			return database.getPrefix()+tableConfig.name();
		
		throw new TableNameNotSet(getClass());
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException {
		return database.con.prepareStatement(query);
	}
	
	public Connection getConnection() {
		return database.con;
	}
	
	public TableConfig getTableConfig() {
		return getClass().getAnnotation(TableConfig.class);
	}
	
	protected Constructor<? extends Row> getRowConstructor() {
		Class<? extends Row> clazz = getTableConfig().rowclass();	
		try {
			return clazz.getConstructor(getClass(), ResultSet.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected List<? extends Row> getAllRows() throws SQLException {
		ArrayList<Row> list = new ArrayList<Row>();
		PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName());
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			try {
				list.add(getRowConstructor().newInstance(rs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
