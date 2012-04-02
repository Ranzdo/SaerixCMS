package com.saerix.cms.libapi;

public abstract class Library {
	LibraryLoader loader;

	public abstract void onEnable();
	
	public LibraryLoader getLibraryLoader() {
		return loader;
	}
}
