package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.ModelLoader;
import com.saerix.cms.libapi.LibraryException;

public class SaerixCMS {
	
	public static void main(String[] args) {
		SaerixCMS cms = new SaerixCMS();
		//properties.load(new FileInputStream("config"));
		cms.getProperties().put("developer_mode", "true");
		cms.getProperties().put("base_url", "http://127.0.0.1");
		cms.getProperties().put("mysql_hostname", "127.0.0.1");
		cms.getProperties().put("mysql_port", "3306");
		cms.getProperties().put("mysql_username", "root");
		cms.getProperties().put("mysql_password", "");
		cms.getProperties().put("mysql_database", "saerixcms");
		cms.getProperties().put("mysql_prefix", "cms_");
		cms.getProperties().put("cms_hostname", "127.0.0.1");
		cms.getProperties().put("port", "8000");
		cms.getProperties().put("secure_port", "443");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			cms.enable();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (LibraryException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private SaerixHttpServer server;
	private GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Properties properties = new Properties();
	private ModelLoader modelLoader;
	
	@SuppressWarnings("unchecked")
	public void enable() throws NumberFormatException, IOException, LibraryException, DatabaseException {
		modelLoader = new ModelLoader(properties);
		
		
		server = new SaerixHttpServer(this, Integer.parseInt(getProperties().get("port").toString()), Integer.parseInt(getProperties().get("secure_port").toString()), getProperties().get("cms_hostname").toString());
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
	
	public ModelLoader getModelLoader() {
		return modelLoader;
	}
	
}
