package com.saerix.cms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
	public static String readResource(String file) throws IOException {
		InputStream is = Util.class.getResourceAsStream(file);
		InputStreamReader isreader = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(isreader);
		String returnThis = "";
		String s;
		while((s = in.readLine()) != null) {
			returnThis += s.concat("\n");
		}
		in.close();
		isreader.close();
		is.close();
		return returnThis;
	}
	
	public static boolean resourceExists(String file) {
		InputStream is = Util.class.getResourceAsStream(file);
		if(is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return is != null;
	}
}
