package com.saerix.cms.controller;

import java.util.Map;

import com.saerix.cms.database.basemodels.HostModel.HostRow;

public class ControllerParameter {
	private final HostRow host;
	private final String[] segments;
	private final Map<String, String> postParameters;
	private final Map<String, String> getParameters;
	
	public ControllerParameter(HostRow host, String[] segments, Map<String, String> postParameters, Map<String, String> getParameters) {
		this.host = host;
		this.segments = segments;
		this.postParameters = postParameters;
		this.getParameters = getParameters;
	}

	public HostRow getHost() {
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
