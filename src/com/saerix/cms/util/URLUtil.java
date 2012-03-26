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
			if(!SaerixCMS.getProperties().get("secure_port").equals("80"))
				port = ":"+SaerixCMS.getProperties().get("secure_port");
		}
		
		return protocol+hostName+port+"/"+segments+glueParameters(parameters);
	}
	
}
