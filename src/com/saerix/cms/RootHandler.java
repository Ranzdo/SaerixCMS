package com.saerix.cms;

import groovy.lang.GroovyObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.tools.GroovyClass;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.Row;
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
		Class<?> clazz = null;
		try {
			clazz = SaerixCMS.getGroovyClassLoader().parseClass("import com.saerix.cms.database.*; class Test { def test() {def model = Database.getTable(\"users\"); model.updateUsername(\"Ranzdo\", \"Taerix\"); }}");
		}
		catch (CompilationFailedException e) {
			e.printStackTrace();
		}
		
		GroovyObject groovyObject = null;
		try {
			groovyObject = (GroovyObject) clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Object[] args = {};
		try {
			Row row = (Row) groovyObject.invokeMethod("test", args);
			os.write(row.toString().getBytes());
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
