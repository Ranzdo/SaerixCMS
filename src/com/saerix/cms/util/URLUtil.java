package com.saerix.cms.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class URLUtil {
	public static String glueParameters(Map<String, String> parameters) {
		if(parameters == null)
			return "";
		
		String returnThis = "?";
		for(Entry<String, String> entry : parameters.entrySet()) {
			try {
				returnThis += URLEncoder.encode(entry.getKey(), "UTF-8")+"="+URLEncoder.encode(entry.getValue(), "UTF-8")+"&";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
				
		return returnThis.substring(0, returnThis.length()-2);
	}
	
	public static String[] splitSegments(String segments) {
		segments = segments.trim();
		String[] a = segments.split("/");
		if(a.length == 0) {
			String[] n = new String[1];
			n[0] = "";
			return n;
		}
		if((a[0].equals("") && a.length == 1) || (!a[0].equals("") && a.length > 1))
			return a;
		
		String[] n = new String[a.length-1];
		
		for(int i = 1; i < a.length;i++) {
			n[i-1] = a[i];
		}
		
		return n;
	}
	
}
