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
import java.util.zip.ZipEntry;

public class LibraryLoader {
	private Map<String, Library> libraries = Collections.synchronizedMap(new HashMap<String, Library>());
	private Vector<Listener> listeners = new Vector<Listener>();
	
	public LibraryLoader() {
		
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
	
	public void reloadLibraries() {
		libraries.clear();
		try {
			File libdir = new File("libraries"+File.separator);
			//TODO finish
			for(File file : libdir.listFiles()) {
				JarFile jar = new JarFile(file);
				for (Enumeration<JarEntry> list = jar.entries(); list.hasMoreElements(); ) {
					JarEntry entry = list.nextElement();
					System.out.println(entry.getName());
			    }
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
