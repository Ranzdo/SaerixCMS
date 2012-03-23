package com.saerix.cms.libapi;

import com.saerix.cms.SaerixCMS;

public abstract class Library {
	public abstract void onEnable();
	
	public LibraryLoader getLibraryLoader() {
		return SaerixCMS.getInstance().getLibraryLoader();
	}
}
