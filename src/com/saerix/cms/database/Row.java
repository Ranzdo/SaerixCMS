package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class Row {
	public HashMap<String, Object> values = new HashMap<String, Object>();
	public Row(ResultSet set) throws SQLException {
		ResultSetMetaData meta =  set.getMetaData();
		for(int i = 1; i <= meta.getColumnCount();i++){
			values.put(meta.getColumnName(i), set.getObject(i));
		}
	}
	
	public Object getValue(String column) {
		return values.get(column);
	}
}