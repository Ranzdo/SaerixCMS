package com.saerix.cms.libapi.events;

import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class PageLoadEvent extends Event {
	private final int hostId;
	private final String hostName;
	private final boolean secure;
	private final String[] segments;
	private final Map<String, String> getParameters;
	private final Map<String, String> postParameters;
	private final HttpExchange handle;
	
	public PageLoadEvent(int hostId, String hostName, boolean secure,
			String[] segments, Map<String, String> getParameters,
			Map<String, String> postParameters, HttpExchange handle) {
		this.hostId = hostId;
		this.hostName = hostName;
		this.secure = secure;
		this.segments = segments;
		this.getParameters = getParameters;
		this.postParameters = postParameters;
		this.handle = handle;
	}

	public int getHostId() {
		return hostId;
	}

	public String getHostName() {
		return hostName;
	}

	public boolean isSecure() {
		return secure;
	}

	public String[] getSegments() {
		return segments;
	}

	public Map<String, String> getGetParameters() {
		return getParameters;
	}

	public Map<String, String> getPostParameters() {
		return postParameters;
	}

	public HttpExchange getHandle() {
		return handle;
	}
}
