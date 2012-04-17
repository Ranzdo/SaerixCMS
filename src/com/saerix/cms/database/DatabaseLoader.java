package com.saerix.cms.database;

import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.basemodels.DatabaseModel;
import com.saerix.cms.database.basemodels.DatabaseModel.DatabaseRow;

public class DatabaseLoader {
	private MainDatabase main;
	private HashMap<String, Database> databases = new HashMap<String, Database>();
	private SaerixCMS instance;
	
	public DatabaseLoader(SaerixCMS instance, Properties properties) throws DatabaseException {
		this.instance = instance;
		this.main = new MainDatabase(this, properties);
	}
	
	public MainDatabase getMainDatabase() {
		return main;
	}
	
	public Database getDatabase(String name) {
		Database database = databases.get(name);
		if(database != null)
			return database;
		
		try {
			DatabaseRow row = (DatabaseRow)((DatabaseModel)main.getModel("databases")).getDatabase(name);
			if(row != null) {
				database = new DatabaseDefinedDatabase(this, row.getId(), row.getProperties());
				databases.put(row.getName(), database);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		return database;
	}
	
	public void registerDatabase(String name, Database database) {
		databases.put(name, database);
	}

	public SaerixCMS getInstance() {
		return instance;
	}
}
