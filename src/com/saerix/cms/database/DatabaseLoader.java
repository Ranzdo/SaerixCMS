package com.saerix.cms.database;

import groovy.lang.GroovyClassLoader;

import java.util.HashMap;
import java.util.Properties;

public class DatabaseLoader {
	private MainDatabase main;
	private HashMap<String, Database> databases = new HashMap<String, Database>();
	
	public DatabaseLoader(GroovyClassLoader classLoader, Properties properties) throws DatabaseException {
		this.main = new MainDatabase(classLoader, properties);
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
}
