package com.saerix.cms.database;

import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.mainmodels.DatabaseModel;
import com.saerix.cms.database.mainmodels.DatabaseModel.DatabaseRow;

public class DatabaseLoader {
	private MainDatabase main;
	private HashMap<String, Database> databases = new HashMap<String, Database>();
	private SaerixCMS instance;
	
	public DatabaseLoader(SaerixCMS instance, Properties properties) throws DatabaseException {
		this.instance = instance;
		this.main = new MainDatabase(this, properties);
		databases.put("main", this.main);
	}
	
	public MainDatabase getMainDatabase() {
		return main;
	}
	
	public Database getDatabase(String name) throws DatabaseException {
		Database database = databases.get(name);
		if(database != null)
			return database;
		
		DatabaseRow row = (DatabaseRow)((DatabaseModel)main.getModel("databases")).getDatabase(name).getRow();
		if(row != null) {
			database = new DatabaseDefinedDatabase(this, row.getId(), row.getProperties());
			databases.put(row.getName(), database);
			return database;
		}
		
		throw new DatabaseException("The database \""+name+"\" was not found.");
	}
	
	public void registerDatabase(String name, Database database) {
		databases.put(name, database);
	}

	public SaerixCMS getInstance() {
		return instance;
	}
}
