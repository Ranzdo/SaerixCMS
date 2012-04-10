package com.saerix.cms.host;

import java.util.Collection;
import java.util.Map;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.libapi.LibraryException;
import com.saerix.cms.libapi.LibraryLoader;
import com.saerix.cms.route.Route;
import com.saerix.cms.route.RouteException;
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.view.EvaluatedView;
import com.saerix.cms.view.View;
import com.saerix.cms.view.ViewException;
import com.saerix.cms.view.ViewNotFoundException;

public abstract class Host {
	private LibraryLoader libraryLoader = new LibraryLoader();
	
	private final SaerixHttpServer server;
	private final String hostName;
	
	protected Host(SaerixHttpServer server, String hostName) throws LibraryException {
		this.server = server;
		this.hostName = hostName;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public SaerixHttpServer getServer() {
		return server;
	}
	
	public String getURL(String segments, Map<String, String> parameters, boolean secure) {
		String protocol;
		String port = "";
		if(secure) {
			protocol = "https://";
			if(!server.getInstance().getProperties().get("secure_port").equals("443"))
				port = ":"+server.getInstance().getProperties().get("secure_port");
		}
		else {
			protocol = "http://";
			if(!server.getInstance().getProperties().get("port").equals("80"))
				port = ":"+server.getInstance().getProperties().get("port");
		}
		
		return protocol+hostName+port+"/"+segments+URLUtil.glueParameters(parameters);
	}

	public Class<? extends Controller> getController(String controllerName) throws ControllerException {
		Class<? extends Controller> controllerClass = getHostController(controllerName);
		return controllerClass;
	}
	
	public Route getRoute(String segments) throws RouteException {
		Route route = getHostRoute(segments);
		if(route != null)
			return route;
		
		String[] segmentArray = URLUtil.splitSegments(segments);
		
		//If we end up here there is no route for the segments, so we pick the default one. If that is not the case neither, then we send a 404 route
		if(segmentArray.length == 1 && segmentArray.length == 2)
			return Route.get404Route();
		
		try {
			return new Route(this, segmentArray[0], segmentArray.length == 1 ? "index" : segmentArray[1]);
		} catch(RouteException e) {
			return Route.get404Route();
		}
	}
	
	public View getView(String viewName) throws ViewException, ViewNotFoundException {
		return new View(getHostView(viewName));
	}
	
	public LibraryLoader getLibraryLoader() {
		return libraryLoader;
	}
	
	protected abstract Class<? extends Controller> getHostController(String controllerName) throws ControllerException;
	protected abstract EvaluatedView getHostView(String viewName) throws ViewException;
	protected abstract Collection<String> loadLibraries();
	protected abstract Route getHostRoute(String segements) throws RouteException;
}
