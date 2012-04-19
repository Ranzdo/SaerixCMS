package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Row {
	Map<String, Object> values = new HashMap<String, Object>();
	Model model;
	
	public Row() {}
	
	public Row(Model model, ResultSet set) throws DatabaseException {
		set(model, set);
	}
	
	public Row(Model model, Map<String, Object> values) {
		set(model, values);
	}
	
	public Row set(Model model, Map<String, Object> values) {
		this.model = model;
		this.values = values;
		return this;
	}

	public Row set(Model model, ResultSet set) throws DatabaseException {
		try {
			this.model = model;
			ResultSetMetaData meta =  set.getMetaData();
			for(int i = 1; i <= meta.getColumnCount();i++){
				values.put(meta.getColumnName(i), set.getObject(i));
			}
			return this;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
	
	public Object get(String column) {
		return values.get(column);
	}
	
	public Set<Entry<String, Object>> getAllValues() {
		return values.entrySet();
	}
	
	public Model model(String tableName) throws DatabaseException {
		return model.database.getModel(tableName);
	}
	
	public String toString() {
		String string = "{";
		for(Entry<String, Object> entry : values.entrySet()) {
			string = string.concat(entry.getKey()+" = "+entry.getValue()+", ");
		}
		return string+"}";
	}
}