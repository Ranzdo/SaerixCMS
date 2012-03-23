package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.libapi.LibraryLoader;

public class SaerixCMS {		
	private static GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static Properties properties = new Properties();
	private static SaerixCMS instance;
	
	public static GroovyClassLoader getGroovyClassLoader() {
		return gClassLoader;
	}
	
	public static ExecutorService executor() {
		return executor;
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static SaerixCMS getInstance() {
		return instance;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		//properties.load(new FileInputStream("config"));
		SaerixCMS.getProperties().put("developer_mode", "true");
		SaerixCMS.getProperties().put("base_url", "http://127.0.0.1");
		SaerixCMS.getProperties().put("mysql_hostname", "62.20.221.96");
		SaerixCMS.getProperties().put("mysql_port", "3306");
		SaerixCMS.getProperties().put("mysql_username", "saerixcms");
		SaerixCMS.getProperties().put("mysql_password", "258012");
		SaerixCMS.getProperties().put("mysql_database", "saerixcms");
		SaerixCMS.getProperties().put("mysql_prefix", "cms_");
		SaerixCMS.getProperties().put("cms_hostname", "127.0.0.1");
		SaerixCMS.getProperties().put("port", "8000");
		SaerixCMS.getProperties().put("secure_port", "443");
		try {
			instance = new SaerixCMS();
			Controller.reloadAllControllers();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private SaerixHttpServer server;
	private LibraryLoader libLoader;
	
	public SaerixCMS() throws IOException {
		libLoader = new LibraryLoader();
		server = new SaerixHttpServer();
	}
	
	public boolean isInDevMode() {
		return SaerixCMS.getProperties().get("developer_mode").equals("true");
	}
	
	public LibraryLoader getLibraryLoader() {
		return libLoader;
	}
}
