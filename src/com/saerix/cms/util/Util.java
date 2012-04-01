package com.saerix.cms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
	public static String readResource(String file) throws IOException {
		InputStream is = Util.class.getResourceAsStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String returnThis = "";
		String s;
		while((s = in.readLine()) != null) {
			returnThis += s.concat("\n");
		}
		is.close();
		return returnThis;
	}
	
	public static boolean resourceExists(String file) {
		return Util.class.getResourceAsStream(file) != null;
	}
}
