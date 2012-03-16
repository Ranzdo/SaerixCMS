package com.saerix.cms.template;

import java.util.HashMap;

public class Taghandler {
	HashMap<String, Object> values = new HashMap<String, Object>();
	
	public Taghandler() {
		
	}
	
	public void assign(String tagName, Object value) {
		if(tagName != null && value != null) {
			if(tagName != "") {
				values.put(tagName, value);
				return;
			}
		}
		throw new IllegalArgumentException();
	}
}
