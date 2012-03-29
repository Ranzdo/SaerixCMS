package com.saerix.cms.libapi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LibraryConfigFile {
	private final String[] MUST_HAVE_KEYS = {
		"main"
	};
	
	private HashMap<String, String> config = new HashMap<String, String>();
	
	LibraryConfigFile(InputStream is, String filename) throws IOException {
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int count = 1;
		while ((line = br.readLine()) != null)   {
			if(line.equals("") || line.startsWith("//"))
				continue;
			
			String[] split = line.split("=");
			if(split.length != 2)
				throw new LibraryException("Could not parse the config file for the library "+filename+". Syntax error at line "+count+" .");
				
			config.put(split[0].trim(), split[1].trim());
			count++;
		}
		in.close();
		
		if(!verfiy())
			throw new LibraryException("Could not parse the config file for the library "+filename+". Missing non-optional variables.");
	}
	
	private boolean verfiy() {
		for(String key : MUST_HAVE_KEYS) {
			if(!config.containsKey(key))
				return false;
		}
		
		return true;
	}
	
	public String get(String key) {
		return config.get(key);
	}
}
