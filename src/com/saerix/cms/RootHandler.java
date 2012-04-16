package com.saerix.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.host.Host;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.route.Route;
import com.saerix.cms.util.HttpError;
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.view.View;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {
	private final SaerixHttpServer server;
	
	public RootHandler(SaerixHttpServer server) {
		this.server = server;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handle(HttpExchange handle) throws IOException {
		try {
			//The host field must be set
			List<String> ahost = handle.getRequestHeaders().get("Host");
			if(ahost == null) {
				send_404(handle);
				return;
			}
			if(ahost.size() == 0) {
				send_404(handle);
				return;
			}
			
			//Gathering all the info we need
			String hostName = ahost.get(0).split(":")[0];
				
			String uriRequest = handle.getRequestURI().toString();
			String[] segmentsAndPara = uriRequest.split("\\?");
			
			String segments = segmentsAndPara[0];
			Map<String, List<String>> getParameters = (Map<String, List<String>>) handle.getAttribute("getparameters");
			
			Map<String, List<String>> postParameters = (Map<String, List<String>>) handle.getAttribute("postparameters");
			
			Map<String, List<String>> cookies = (Map<String, List<String>>) handle.getAttribute("cookies");
			
			Host host = server.getHost(hostName);
			
			String[] segmentsArray = URLUtil.splitSegments(segments);
			
			//Setting up deafult headers
			handle.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
			
			
			//Run the library listeners
			PageLoadEvent pageLoadEvent = new PageLoadEvent(host, false, segmentsArray, getParameters, postParameters, cookies, handle);
			
			host.onPageLoad(pageLoadEvent);
			
			Route route = host.getRoute(pageLoadEvent);
			
			Controller controller = route.invokeRoute();
			
			int returnCode = controller.getReturnCode();
			
			handle.sendResponseHeaders(returnCode, 0);
			OutputStream os = handle.getResponseBody();
			os.write(View.mergeViews(controller.getViews()).getBytes());
			os.flush();
			os.close();
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			e.printStackTrace();
			handle.sendResponseHeaders(500, 0);
			OutputStream responseBody = handle.getResponseBody();
			PrintStream ps = new PrintStream(responseBody);
			ps.write((HttpError.RETURN_500+"\n\n").getBytes());
			ps.write("<code>".getBytes());
			e.printStackTrace(ps);
			ps.write("</code>".getBytes());
			ps.flush();
			ps.close();
			responseBody.flush();
			responseBody.close();
		}
	}
	
	private void send_404(HttpExchange handle) throws IOException {
		handle.sendResponseHeaders(404, 0);
		OutputStream responseBody = handle.getResponseBody();
		responseBody.write((HttpError.RETURN_404).getBytes());
		responseBody.flush();
		responseBody.close();
	}
}
