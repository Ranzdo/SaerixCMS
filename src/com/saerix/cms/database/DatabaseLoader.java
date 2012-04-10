package com.saerix.cms.database;

import groovy.lang.GroovyClassLoader;

import java.util.HashMap;
import java.util.Properties;

import org.codehaus.groovy.control.CompilationFailedException;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.basemodels.ControllerModel;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.ModelModel;
import com.saerix.cms.database.basemodels.ModelModel.ModelRow;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.UserModel;
import com.saerix.cms.database.basemodels.ViewModel;

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
