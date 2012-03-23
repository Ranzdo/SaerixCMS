package com.saerix.cms.controller;

import java.util.Map;

public class ControllerParameter {
	private final int hostId;
	private final String hostValue;
	private final String[] segments;
	private final Map<String, String> postParameters;
	private final Map<String, String> getParameters;
	private final boolean secure;
	
	public ControllerParameter(int hostId, String hostValue, String[] segments, Map<String, String> postParameters, Map<String, String> getParameters, boolean secure) {
		this.hostId = hostId;
		this.hostValue = hostValue;
		this.segments = segments;
		this.postParameters = postParameters;
		this.getParameters = getParameters;
		this.secure = secure;
	}

	public int getHostId() {
		return hostId;
	}
	
	public String getHostValue() {
		return hostValue;
	}

	public String[] getSegments() {
		return segments;
	}

	public Map<String, String> getPostParameters() {
		return postParameters;
	}

	public Map<String, String> getGetParameters() {
		return getParameters;
	}

	public boolean isSecure() {
		return secure;
	}
}
