package com.saerix.cms.controller;

import java.util.Map;

public class ControllerParameter {
	private final String host;
	private final String[] segments;
	private final Map<String, String> postParameters;
	private final Map<String, String> getParameters;
	
	public ControllerParameter(String host, String[] segments, Map<String, String> postParameters, Map<String, String> getParameters) {
		this.host = host;
		this.segments = segments;
		this.postParameters = postParameters;
		this.getParameters = getParameters;
	}

	public String getHost() {
		return host;
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
}
