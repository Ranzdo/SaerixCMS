package com.saerix.cms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.HostModel.HostRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteType;
import com.saerix.cms.libapi.Listener;
import com.saerix.cms.libapi.events.PageLoadEvent;
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
			boolean local = SaerixCMS.getProperties().get("cms_hostname").equals(hostValue);
				
			String uriRequest = handle.getRequestURI().toString();
			String[] segmentsAndPara = uriRequest.split("\\?");
			
			String segments = segmentsAndPara[0];
			Map<String, List<String>> getParameters = (Map<String, List<String>>) handle.getAttribute("getparameters");
			
			Map<String, List<String>> postParameters = (Map<String, List<String>>) handle.getAttribute("postparameters");
			
			Map<String, List<String>> cookies = (Map<String, List<String>>) handle.getAttribute("cookies");
			
			String[] segmentsArray = segments.split("/");
			int hostId;
			if(local)
				hostId = -1;
			else {
				HostRow host = (HostRow) ((HostModel) Database.getTable("hosts")).getHost(hostValue);
				if(host == null) {
					HttpError.send404(handle);
					return;
				}
				else
					hostId = (Integer) host.getValue("host_id");
			}
			
			//Run the library listeners
			PageLoadEvent pageLoadEvent = new PageLoadEvent(hostId, hostValue, false, segmentsArray, getParameters, postParameters, cookies, handle);
			for(Listener listener : SaerixCMS.getInstance().getLibraryLoader().getListeners()) {
				listener.onPageLoad(pageLoadEvent);
			}
			
			
			RouteType routeType = RouteType.CONTROLLER;
			String routeController = null;
			String routeMethod = null;
			String fullValue = "";
			
			if(local) {
				BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/com/saerix/cms/cms/routes")));
				String line;
				while ((line = br.readLine()) != null)   {
					String[] values = line.split("=");
					if(segmentsAndPara[0].equals(values[0].trim())) {
						String[] route = values[1].trim().split("/");
						routeController = route[1];
						routeMethod = route[2];
						break;
					}
				}
				br.close();
			}
			else {
				RouteRow routerow = ((RouteModel) Database.getTable("routes")).getRoute(hostId, segments);
				if(routerow == null) {
					
					
					
				}
				fullValue = routerow.getRouteValue();
				String[] value = fullValue.split(":");
				routeType = routerow.getType();
				routeController = value[0];
				routeMethod = value[1];
			}
			
			if(routeController == null || routeMethod == null) {
				HttpError.send404(handle);
				return;
			}
			
			if(routeType == RouteType.REDIRECT) {
				redirect(handle, fullValue);
				return;
			}
			else if(routeType == RouteType.CONTROLLER) {
				Class<? extends Controller> controllerClazz;
				if(local)
					controllerClazz = Controller.getLocalController(routeController);
				else
					controllerClazz = Controller.getController(Integer.parseInt(routeController));
				if(controllerClazz == null) {
					HttpError.send404(handle);
					return;
				}
				Controller controller;
				try {
					controller = Controller.invokeController(controllerClazz, routeMethod, pageLoadEvent);
				}
				catch(NoSuchMethodException e) {
					HttpError.send404(handle);
					return;
				}
				
				if(controller.willShow404()) {
					HttpError.send404(handle);
					return;
				}
				
				if(controller.willRedirect() != null) {
					redirect(handle, controller.willRedirect());
					return;
				}
				
				handle.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
				
				handle.sendResponseHeaders(200, 0);
				OutputStream os = handle.getResponseBody();
				os.write(View.mergeViews(controller.getViews()).getBytes());
				os.flush();
				os.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			HttpError.send500(handle, e);
		}
	}
	
	private void redirect(HttpExchange handle, String url) throws IOException {
		handle.getResponseHeaders().add("Location", url);
		handle.sendResponseHeaders(301, 0);
		handle.getResponseBody().close();
	}
}
