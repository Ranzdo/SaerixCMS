package com.saerix.cms.libapi;

import com.saerix.cms.SaerixCMS;

public abstract class Library {
	LibraryConfig config;

	public abstract void onEnable();
	
	public LibraryLoader getLibraryLoader() {
		return SaerixCMS.getInstance().getLibraryLoader();
	}
}
