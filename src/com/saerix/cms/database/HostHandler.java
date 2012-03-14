package com.saerix.cms.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class HostHandler {
	private Host defaultHost;
	private HashMap<String, Host> hosts = new HashMap<String, Host>();
	
	public HostHandler(Host defaultHost) {
		this.defaultHost = defaultHost;
	}
	
	public Host getHost(String hostName) {
		Host h = hosts.get(hostName);
		return h == null ? defaultHost : h;
	}
	
	public Host createHost(String hostName) {
		Host h = new Host(hostName);
		hosts.put(hostName, h);
		return h;
	}
	
	public boolean doesHostExists(String hostName) {
		return hosts.containsKey(hostName);
	}
	
	public List<Host> getHosts() {
		ArrayList<Host> list = new ArrayList<Host>();
		for(Entry<String, Host> entry : hosts.entrySet())
			list.add(entry.getValue());
		return list;
	}
}
