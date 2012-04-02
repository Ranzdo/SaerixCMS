package com.saerix.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.HostModel.HostRow;
import com.saerix.cms.libapi.Listener;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.route.Route;
import com.saerix.cms.util.HttpError;
import com.saerix.cms.util.URLUtil;
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
			
			String[] segmentsArray = URLUtil.splitSegments(segments);
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
			
			Route route = Route.getRoute(hostId, segments);
			Controller controller = route.invokeRoute(pageLoadEvent);
			
			int returnCode = controller.getReturnCode();
			
			if(returnCode == 404) {
				HttpError.send404(handle);
			}
			else if(returnCode == 302) {
				handle.getResponseHeaders().add("Location", controller.redirectTo());
				handle.sendResponseHeaders(301, 0);
				handle.getResponseBody().close();
			}
			else {
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
