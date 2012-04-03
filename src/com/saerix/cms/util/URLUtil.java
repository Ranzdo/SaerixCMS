package com.saerix.cms.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.saerix.cms.SaerixCMS;

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
	
	public static String getURL(String hostName, String segments, Map<String, String> parameters, boolean secure) {
		String protocol;
		String port = "";
		if(secure) {
			protocol = "https://";
			if(!SaerixCMS.getProperties().get("secure_port").equals("443"))
				port = ":"+SaerixCMS.getProperties().get("secure_port");
		}
		else {
			protocol = "http://";
			if(!SaerixCMS.getProperties().get("port").equals("80"))
				port = ":"+SaerixCMS.getProperties().get("port");
		}
		
		return protocol+hostName+port+"/"+segments+glueParameters(parameters);
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
