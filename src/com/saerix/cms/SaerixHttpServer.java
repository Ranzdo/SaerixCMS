package com.saerix.cms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.saerix.cms.util.ParameterFilter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class SaerixHttpServer  {
	
	private final HttpServer server;

	public SaerixHttpServer(int port) throws IOException {
	    InetSocketAddress addr = new InetSocketAddress(port);
	    server = HttpServer.create(addr, 0);
	    
	    HttpContext pagecontext = server.createContext("/", new RootHandler());
	    pagecontext.getFilters().add(new ParameterFilter());
	    
	    server.setExecutor(Executors.newCachedThreadPool());
	    server.start();
	}
	
	public HttpServer getServer() {
		return server;
	}
}
