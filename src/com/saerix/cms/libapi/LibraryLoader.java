package com.saerix.cms.libapi;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.sessionlib.SessionLibrary;

public class LibraryLoader {
	private Class<?>[] baseLibraries = {
		SessionLibrary.class
	};
	
	private Map<String, Library> libraries = Collections.synchronizedMap(new HashMap<String, Library>());
	private Vector<Listener> listeners = new Vector<Listener>();
	
	public LibraryLoader() {
		reloadLibraries();
	}
	
	public void registerListener(Listener listener) {
		listeners.add(listener);
	}
	
	public Library getLib(String name) {
		return libraries.get(name);
	}
	
	public Collection<Listener> getListeners() {
		return listeners;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadLibraries() {
		libraries.clear();
		
		//Load base first
		for(Class<?> clazz : baseLibraries) {
			loadLibrary((Class<? extends Library>) clazz, null);
		}
		
		try {
			File libdir = new File("libraries"+File.separator);
			if(!libdir.exists())
				return;
			
			for(File file : libdir.listFiles()) {
				JarFile jar;
				try {
					jar = new JarFile(file);
				}
				catch(ZipException e) {
					continue;
				}
				for (Enumeration<JarEntry> list = jar.entries(); list.hasMoreElements(); ) {
					JarEntry entry = list.nextElement();
					if(entry.getName().equals("plugin.conf")) {
						LibraryConfigFile libConfig = new LibraryConfigFile(jar.getInputStream(entry), jar.getName());
						
						SaerixCMS.getGroovyClassLoader().addURL(file.toURI().toURL());
						
						try {
							Class<?> mainClass = SaerixCMS.getGroovyClassLoader().loadClass(libConfig.get("main"));
							
							if(!Library.class.isAssignableFrom(Library.class))
								throw new LibraryException("Main class is not assignable with the Library class.");
							
							Class<? extends Library> clazz = (Class<? extends Library>) mainClass;
							
							loadLibrary(clazz, libConfig);
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						
						break;
					}
			    }
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadLibrary(Class<? extends Library> clazz, LibraryConfigFile libConfig) {
		try {
			if(!clazz.isAnnotationPresent(LibraryConfig.class))
				throw new LibraryException("LibraryConfig annonation missng in the class "+clazz.getName());
				
			Library lib = clazz.newInstance();
			lib.config = libConfig;
			lib.loader = this;
			lib.onEnable();
			libraries.put(clazz.getAnnotation(LibraryConfig.class).name(), lib);
		} catch (LibraryException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
