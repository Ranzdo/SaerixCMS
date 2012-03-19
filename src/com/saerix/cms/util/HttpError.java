package com.saerix.cms.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sun.net.httpserver.HttpExchange;

public final class HttpError {
	private static final String RETURN_404 = "404 - Not Found";
	private static final String RETURN_500 = "500 - Internal server error";
	
	public static void send404(HttpExchange handle) throws IOException {
		handle.sendResponseHeaders(404, 0);
		OutputStream responseBody = handle.getResponseBody();
		responseBody.write(RETURN_404.getBytes());
		responseBody.flush();
		responseBody.close();
	}
	
	public static void send500(HttpExchange handle, Exception e) throws IOException {
		handle.sendResponseHeaders(500, 0);
		OutputStream responseBody = handle.getResponseBody();
		PrintStream ps = new PrintStream(responseBody);
		ps.write((RETURN_500+"\n\n").getBytes());
		ps.write("<code>".getBytes());
		e.printStackTrace(ps);
		ps.flush();
		ps.close();
		responseBody.flush();
		responseBody.close();
	}
}
