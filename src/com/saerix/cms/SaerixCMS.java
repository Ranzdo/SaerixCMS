package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;

public class SaerixCMS {
	private static GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	public static GroovyClassLoader getGroovyClassLoader() {
		return gClassLoader;
	}
	
	
	public static void main(String[] args) {
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
