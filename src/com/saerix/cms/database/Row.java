package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

public class Row {
	private HashMap<String, Object> values = new HashMap<String, Object>();
	
	public Row() {}
	
	protected Row(ResultSet set) throws SQLException {
		set(set);
	}
	
	protected Row set(ResultSet set) throws SQLException {
		ResultSetMetaData meta =  set.getMetaData();
		for(int i = 1; i <= meta.getColumnCount();i++){
			values.put(meta.getColumnName(i), set.getObject(i));
		}
		return this;
	}
	
	public Object getValue(String column) {
		return values.get(column);
	}
	
	public String toString() {
		String string = "{";
		for(Entry<String, Object> entry : values.entrySet()) {
			string = string.concat(entry.getKey()+" = "+entry.getValue()+", ");
		}
		return string+"}";
	}
}