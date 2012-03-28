package com.saerix.cms.libapi.events;

import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class PageLoadEvent extends Event {
	private final int hostId;
	private final String hostName;
	private final boolean secure;
	private final String[] segments;
	private final Map<String, List<String>> getParameters;
	private final Map<String, List<String>> postParameters;
	private final Map<String, List<String>> cookies;
	private final HttpExchange handle;
	
	public PageLoadEvent(int hostId, String hostName, boolean secure,
			String[] segments, Map<String, List<String>> getParameters2,
			Map<String, List<String>> postParameters2, Map<String, List<String>> cookies2, HttpExchange handle) {
		this.hostId = hostId;
		this.hostName = hostName;
		this.secure = secure;
		this.segments = segments;
		this.getParameters = getParameters2;
		this.postParameters = postParameters2;
		this.cookies = cookies2;
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

	public Map<String, List<String>> getGetParameters() {
		return getParameters;
	}

	public Map<String, List<String>> getPostParameters() {
		return postParameters;
	}
	
	public Map<String, List<String>> getCookies() {
		return cookies;
	}


	public HttpExchange getHandle() {
		return handle;
	}
}
