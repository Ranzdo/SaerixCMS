package com.saerix.cms;

import groovy.lang.GroovyObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.RouteModel.RouteRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteType;
import com.saerix.cms.util.HttpError;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange handle) throws IOException {
		try {
			List<String> ahost = handle.getRequestHeaders().get("Host");
			if(ahost == null) {
				HttpError.send404(handle);
				return;
			}
			if(ahost.size() == 0) {
				HttpError.send404(handle);
				return;
			}
			
			String hostValue = ahost.get(0).split(":")[0];
			Row host = ((HostModel) Database.getTable("hosts")).getHost(hostValue);
			int hostId = (Integer) host.getValue("host_id");
			
			String segments = handle.getRequestURI().toString();
			String[] segmentsArray = {"/",""};
			if(!segments.equals("/"))
				segmentsArray = segments.split("/");
			
			RouteRow routerow = ((RouteModel) Database.getTable("routes")).getRoute(hostId, segments);
			
			if(routerow == null) {
				HttpError.send404(handle);
				return;
			}
			
			RouteType routeType = routerow.getType();
			
			if(routeType == RouteType.REDIRECT) {
				handle.getResponseHeaders().add("Location", routerow.getRouteValue());
				handle.sendResponseHeaders(301, 0);
				handle.getResponseBody().close();
				return;
			}
			else if(routeType == RouteType.CONTROLLER) {
				//TODO
			}
			
			
			OutputStream os = handle.getResponseBody();
			Class<?> clazz = null;
			try {
				clazz = SaerixCMS.getGroovyClassLoader().parseClass("import com.saerix.cms.database.*; class Test { def test() {def model = Database.getTable(\"users\"); return model.getUser(\"Taerix\"); }}");
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
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
