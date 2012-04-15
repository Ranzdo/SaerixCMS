package com.saerix.cms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
	
	public static String glue(String[] array, String glue, int offset) {
		StringBuilder builder = new StringBuilder();
		for(int i = offset; i < array.length;i++) {
			builder.append(array[i]+glue);
		}
		return builder.toString();
	}
	
	public static String glue(String[] array, String glue) {
		return glue(array, glue, 0);
	}
	
	  public static String getStackTrace(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
	  }
}
