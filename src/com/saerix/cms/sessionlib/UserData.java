package com.saerix.cms.sessionlib;

import java.util.HashMap;
import java.util.Map;

public class UserData {
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public void set(String key, Object value) {
		data.put(key, value);
	}
	
	public boolean getBoolean(String key) {
		Object o = data.get(key);
		return o == null ? false : o instanceof Boolean ? (Boolean) o : false;
	}
	
	public String getString(String key) {
		Object o = data.get(key);
		return o == null ? "" : o instanceof String ? (String) o : o.toString();
	}
	
	public Object getObject(String key) {
		return data.get(key);
	}
}
