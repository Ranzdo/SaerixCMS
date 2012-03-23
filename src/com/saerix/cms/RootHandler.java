package com.saerix.cms;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerParameter;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.HostModel.HostRow;
import com.saerix.cms.database.basemodels.RouteModel;
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
			int hostId;
			if(local)
				hostId = -1;
			else {
				HostRow host = (HostRow) ((HostModel) Database.getTable("hosts")).getHost(hostValue);
				hostId = (Integer) host.getValue("host_id");
			}
			
			//Run the library listeners
			PageLoadEvent pageLoadEvent = new PageLoadEvent(hostId, hostValue, false, segmentsArray, getParameters, postParameters, handle);
			for(Listener listener : SaerixCMS.getInstance().getLibraryLoader().getListeners()) {
				listener.onPageLoad(pageLoadEvent);
			}
			
			
			RouteType routeType = RouteType.CONTROLLER;
			String routeController = null;
			String routeMethod = null;
			String fullValue = "";
			
			if(local) {
				FileInputStream fstream = new FileInputStream("cms/routes");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
				in.close();
			}
			else {
				RouteRow routerow = ((RouteModel) Database.getTable("routes")).getRoute(hostId, segments);
				if(routerow == null) {
					HttpError.send404(handle);
					return;
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
				handle.getResponseHeaders().add("Location", fullValue);
				handle.sendResponseHeaders(301, 0);
				handle.getResponseBody().close();
				return;
			}
			else if(routeType == RouteType.CONTROLLER) {
				ControllerParameter controllerParameters = new ControllerParameter(hostId, hostValue, segmentsArray, postParameters, getParameters, false);
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
					controller = Controller.invokeController(controllerClazz, routeMethod, controllerParameters);
				}
				catch(NoSuchMethodException e) {
					HttpError.send404(handle);
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
}
