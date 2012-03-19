package com.saerix.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.RouteModel.RouteRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteType;
import com.saerix.cms.util.HttpError;
import com.saerix.cms.view.View;
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
				String[] value = routerow.getRouteValue().split(":");
				Class<? extends Controller> controllerclazz = Controller.getController(Integer.parseInt(value[0]));
				
				Controller controller = controllerclazz.newInstance();
				Method method = controllerclazz.getMethod(value[1]);
				method.invoke(controller);
				
				StringBuilder finalContent = new StringBuilder();
				for(View view : controller.getViews()) {
					finalContent.append(view.evaluate());
				}
				
				handle.sendResponseHeaders(200, 0);
				OutputStream os = handle.getResponseBody();
				os.write(finalContent.toString().getBytes());
				os.flush();
				os.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			HttpError.send500(handle, e);
		}
	}

}
