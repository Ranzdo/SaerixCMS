package com.saerix.cms.database;

import java.lang.reflect.Constructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Model {
	public Database database;
	private TableConf tableConfig;
	private String primaryKeyColumn;
	
	public Model() {
		TableConfig atableConfig = getClass().getAnnotation(TableConfig.class);
		if(atableConfig != null)
			tableConfig = new TableConf(atableConfig);
		else
			throw new TableConfigNotSet(getClass());
	}
	
	Model(String tableName) {
		tableConfig = new TableConf(tableName, false, Row.class);
	}
	
	public void setup() {
		try {
			ResultSet keys = database.con.getMetaData().getPrimaryKeys(null, null, getTableName());
			if(keys.first())
				primaryKeyColumn = keys.getString("COLUMN_NAME");
			else
				throw new TableHasNoPrimaryKeys(getTableName());
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getTableName() {
		return database.getPrefix()+tableConfig.getTableName();
	}
	
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException {
		PreparedStatement ps = database.con.prepareStatement(query);
		ps.closeOnCompletion();
		return ps;
	}
	
	public Connection getConnection() {
		return database.con;
	}
	
	protected Constructor<? extends Row> getRowConstructor() {
		Class<? extends Row> clazz = getTableConfig().getRowClass();	
		try {
			return clazz.getConstructor(getClass(), ResultSet.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public TableConf getTableConfig() {
		return tableConfig;
	}
	
	public Object insertRow(Map<String, Object> values) throws SQLException {
		String query = "INSERT INTO "+getTableName()+" (";
		int counter = 0;
		for(Entry<String, Object> entry : values.entrySet()) {
			query = query.concat(entry.getKey());
			counter++;
			if(counter != values.size()) {
				query = query.concat(",");
			}
		}
		query = query.concat(") VALUES (");
		for(int i = 0; i < values.size();i++) {
			query = query.concat("?");
			if(i != values.size()-1) {
				query = query.concat(",");
			}
		}
		query = query.concat(")");
		PreparedStatement ps = database.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		counter = 1;
		for(Entry<String, Object> entry : values.entrySet()) {
			ps.setObject(counter, entry.getValue());
			counter++;
		}
		ps.executeUpdate();

		ResultSet rs = ps.getGeneratedKeys();
		
		Object treturn = null;
		
		if(rs.first())
			treturn = rs.getObject(1);
		
		rs.close();
		ps.close();
		
		return treturn;
	}
	
	public List<? extends Row> getAllRows() throws SQLException {
		ArrayList<Row> list = new ArrayList<Row>();
		PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName());
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			try {
				list.add(getRowConstructor().newInstance(this, rs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public Row getRow(Object primaryKey) throws SQLException {
		PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName()+" WHERE "+primaryKeyColumn+" = ?");
		ps.setObject(1, primaryKey);
		ResultSet rs = ps.executeQuery();
		if(rs.first()) {
			try {
				return getRowConstructor().newInstance(this, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				rs.close();
			}
		}
		
		return null;
	}
	
	
	public class Row {
		private HashMap<String, Object> values = new HashMap<String, Object>();
		
		protected Row(ResultSet set) throws SQLException {
			ResultSetMetaData meta =  set.getMetaData();
			for(int i = 1; i <= meta.getColumnCount();i++){
				values.put(meta.getColumnName(i), set.getObject(i));
			}
		}
		
		public Object getValue(String column) {
			return values.get(column);
		}
		
		public void update(String column, Object value) throws SQLException {
			PreparedStatement ps = Model.this.prepareStatement("UPDATE "+getTableName()+" SET "+column+" = ? WHERE "+primaryKeyColumn+" = ?");
			ps.setObject(1, values.get(primaryKeyColumn));
			ps.setObject(2, value);
			ps.executeUpdate();
			values.put(column, value);
		}
		
		public String toString() {
			String string = "{";
			for(Entry<String, Object> entry : values.entrySet()) {
				string = string.concat(entry.getKey()+" = "+entry.getValue()+", ");
			}
			return string+"}";
		}
	}
}
