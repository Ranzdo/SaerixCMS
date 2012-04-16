package com.saerix.cms.database;

import java.util.HashMap;
import java.util.Properties;

import com.saerix.cms.SaerixCMS;

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
		return null;
	}
	
	public void registerDatabase(String name, Database database) {
		databases.put(name, database);
	}

	public SaerixCMS getInstance() {
		return instance;
	}
}
