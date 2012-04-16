package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.DatabaseLoader;
import com.saerix.cms.host.HostException;

public class SaerixCMS {
	
	public static void main(String[] args) {
		SaerixCMS cms = new SaerixCMS();
		//properties.load(new FileInputStream("config"));
		cms.getProperties().put("developer_mode", "true");
		cms.getProperties().put("mysql_hostname", "62.20.221.96");
		cms.getProperties().put("mysql_port", "3306");
		cms.getProperties().put("mysql_username", "saerixcms");
		cms.getProperties().put("mysql_password", "258012");
		cms.getProperties().put("mysql_database", "saerixcms");
		cms.getProperties().put("mysql_prefix", "cms_");
		cms.getProperties().put("default_hostname", "127.0.0.1");
		cms.getProperties().put("port", "8000");
		cms.getProperties().put("secure_port", "443");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			cms.enable();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (HostException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private SaerixHttpServer server;
	private GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Properties properties = new Properties();
	private DatabaseLoader databaseLoader;
	
	public void enable() throws NumberFormatException, IOException, DatabaseException, HostException {
		databaseLoader = new DatabaseLoader(this, properties);
		
		
		server = new SaerixHttpServer(this, Integer.parseInt(getProperties().get("port").toString()), Integer.parseInt(getProperties().get("secure_port").toString()), getProperties().get("default_hostname").toString());
	}
	
	public boolean isInDevMode() {
		return getProperties().get("developer_mode").equals("true");
	}
	
	public GroovyClassLoader getGroovyClassLoader() {
		return gClassLoader;
	}
	
	public ExecutorService executor() {
		return executor;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public DatabaseLoader getDatabaseLoader() {
		return databaseLoader;
	}
	
}
