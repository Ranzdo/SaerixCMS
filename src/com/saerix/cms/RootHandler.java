package com.saerix.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.TemplateTable;
import com.saerix.cms.util.HttpError;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange handle) throws IOException {
		List<String> ahost = handle.getRequestHeaders().get("Host");
		if(ahost == null) {
			HttpError.send404(handle);
			return;
		}
		if(ahost.size() == 0) {
			HttpError.send404(handle);
			return;
		}
		
		String host = ahost.get(0).split(":")[0];
		
		handle.sendResponseHeaders(200, 0);
		
		OutputStream os = handle.getResponseBody();
		os.write(((TemplateTable)Database.getTable("templates")).getTemplate("hahaha").toString().getBytes());
		os.flush();
		os.close();
		
	}

}
