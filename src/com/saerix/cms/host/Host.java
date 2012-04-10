package com.saerix.cms.host;

import java.util.Collection;
import java.util.Map;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.libapi.LibraryLoader;
import com.saerix.cms.libapi.Listener;
import com.saerix.cms.libapi.events.PageLoadEvent;
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
	
	protected Host(SaerixHttpServer server, String hostName) {
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
	
	public Route getRoute(PageLoadEvent pageLoadEvent) throws RouteException {
		String[] segmentArray = pageLoadEvent.getSegments();
		
		Route route = getHostRoute(pageLoadEvent);
		if(route != null)
			return route;
		
		//If we end up here there is no route for the segments, so we pick the default one. If that is not the case neither, then we send a 404 route
		if(segmentArray.length == 0)
			return Route.get404Route();
		
		try {
			return new Route(this, segmentArray[0], segmentArray.length == 1 ? "index" : segmentArray[1], pageLoadEvent);
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

	public void onPageLoad(PageLoadEvent pageLoadEvent) {
		for(Listener listener : getLibraryLoader().getListeners()) {
			listener.onPageLoad(pageLoadEvent);
		}
	}
	
	protected abstract Class<? extends Controller> getHostController(String controllerName) throws ControllerException;
	protected abstract EvaluatedView getHostView(String viewName) throws ViewException;
	protected abstract Collection<String> loadLibraries();
	protected abstract Route getHostRoute(PageLoadEvent pageLoadEvent) throws RouteException;
}
