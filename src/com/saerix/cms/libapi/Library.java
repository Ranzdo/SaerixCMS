package com.saerix.cms.libapi;

public abstract class Library {
	LibraryLoader loader;
	LibraryConfig config;

	public abstract void onEnable();
	
	public LibraryLoader getLibraryLoader() {
		return loader;
	}
	
	public LibraryConfig getConfig() {
		return config;
	}
}
