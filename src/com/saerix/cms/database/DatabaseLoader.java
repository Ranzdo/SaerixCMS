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
		reSyncWithDatabase();
	}
	
	public void reSyncWithDatabase() throws DatabaseException {
		synchronized (databases) {
			databases.clear();
			for(Row row : ((DatabaseModel)main.getModel("databases")).getDatabases().getRows()) {
				DatabaseRow drow = (DatabaseRow)row;
				databases.put(drow.getName(), new DatabaseDefinedDatabase(this, drow.getName(), drow.getProperties()));
			}
		}
	}
	
	public MainDatabase getMainDatabase() {
		return main;
	}
	
	public Database getDatabase(String name) throws DatabaseException {
		if(name.equalsIgnoreCase("main"))
			return getMainDatabase();
		
		Database database = databases.get(name);
		if(database != null)
			return database;
		
		throw new DatabaseException("The database \""+name+"\" was not found.");
	}
	
	public void registerDatabase(String name, Database database) throws DatabaseException {
		if(databases.get(name) != null)
			throw new DatabaseException("Could not register the database \""+name+"\", it's name is already taken.");
		
		databases.put(name, database);
	}

	public SaerixCMS getInstance() {
		return instance;
	}
}
