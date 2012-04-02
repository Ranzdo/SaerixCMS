package com.saerix.cms.libapi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Model;
import com.saerix.cms.sessionlib.SessionLibrary;

public class LibraryLoader {
	private Class<?>[] baseLibraries = {
		SessionLibrary.class
	};
	
	private Map<String, Library> libraries = Collections.synchronizedMap(new HashMap<String, Library>());
	private Vector<Listener> listeners = new Vector<Listener>();
	
	@SuppressWarnings("unchecked")
	public LibraryLoader() throws LibraryException {
		for(Class<?> clazz : baseLibraries) {
			loadLibrary((Class<? extends Library>) clazz);
		}
	}
	
	public void registerListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void registerController(String segments, Class<? extends Controller> controller) {
		//TODO
	}
	
	public void registerModel(Class<? extends Model> model) {
		//TODO
	}
	
	public Library getLib(String name) {
		return libraries.get(name);
	}
	
	public Collection<Listener> getListeners() {
		return listeners;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadLibraries() throws LibraryException {
		libraries.clear();
		
		for(Class<?> clazz : baseLibraries) {
			loadLibrary((Class<? extends Library>) clazz);
		}
	}
	
	public void loadLibrary(Class<? extends Library> clazz) throws LibraryException {
		try {
			if(!clazz.isAnnotationPresent(LibraryConfig.class))
				throw new LibraryException("LibraryConfig annonation missing in the class "+clazz.getName());
			
			Library lib = clazz.newInstance();
			lib.loader = this;
			lib.onEnable();
			libraries.put(clazz.getAnnotation(LibraryConfig.class).name(), lib);
		} catch (InstantiationException e) {
			throw (LibraryException) new LibraryException().initCause(e);
		} catch (IllegalAccessException e) {
			throw (LibraryException) new LibraryException().initCause(e);
		}
	}
}
