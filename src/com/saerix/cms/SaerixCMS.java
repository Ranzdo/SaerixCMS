package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaerixCMS {		
	private static GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static Properties properties = new Properties();
	
	
	public static GroovyClassLoader getGroovyClassLoader() {
		return gClassLoader;
	}
	
	public static ExecutorService executor() {
		return executor;
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		properties.load(new FileInputStream("config"));
		try {
			new SaerixCMS();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SaerixHttpServer server;
	
	public SaerixCMS() throws IOException {
		server = new SaerixHttpServer(8000);
	}
}
