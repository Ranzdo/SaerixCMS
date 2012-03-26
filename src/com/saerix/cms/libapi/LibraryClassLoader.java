package com.saerix.cms.libapi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

public class LibraryClassLoader extends URLClassLoader {
	
	public LibraryClassLoader(URLClassLoader systemClassLoader) {
		super(systemClassLoader.getURLs());
	}

	public void addLibJar(File file) {
		try {
			addURL(file.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
