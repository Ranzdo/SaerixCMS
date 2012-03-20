package com.saerix.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerParameter;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void handle(HttpExchange handle) throws IOException {
		try {
			if(!handle.getRequestMethod().equals("POST") && !handle.getRequestMethod().equals("GET")) {
				HttpError.send404(handle);
				return;
			}
			
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
			
			String uriRequest = handle.getRequestURI().toString();
			String[] segmentsAndPara = uriRequest.split("\\?");
			
			String segments = segmentsAndPara[0];
			Map<String, String> getParameters = new HashMap<String, String>();
			if(segmentsAndPara.length == 2) {
				for(String parameter : segmentsAndPara[1].split("&")) {
					String[] para = parameter.split("=");
					if(para.length == 2) {
						getParameters.put(URLDecoder.decode(para[0], "UTF-8"), URLDecoder.decode(para[1], "UTF-8"));
					}
				}
			}
			
			Map<String, String> postParameters = new HashMap<String, String>();
			if(handle.getRequestMethod().equals("POST")) {
				Object o = handle.getAttribute("parameters");
				if(o instanceof Map<?, ?>) {
					postParameters = (Map<String, String>) o;
				}
			}
			
			String[] segmentsArray = segments.split("/");
			
			ControllerParameter controllerParameters = new ControllerParameter(hostValue, segmentsArray, getParameters, postParameters);
			
			
			Row host = ((HostModel) Database.getTable("hosts")).getHost(hostValue);
			int hostId = (Integer) host.getValue("host_id");
			
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
				Controller controller = Controller.invokeController(Integer.parseInt(value[0]), value[1], controllerParameters);
				
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
