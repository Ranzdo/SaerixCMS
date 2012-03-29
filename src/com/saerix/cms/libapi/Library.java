package com.saerix.cms.libapi;

public abstract class Library {
	LibraryLoader loader;
	LibraryConfigFile config;

	public abstract void onEnable();
	
	public LibraryLoader getLibraryLoader() {
		return loader;
	}
	
	public LibraryConfigFile getConfig() {
		return config;
	}
}
