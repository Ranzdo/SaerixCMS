package com.saerix.cms.host;

import java.util.Collection;

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
	
	private final String hostName;
	
	protected Host(String hostName) throws LibraryException {
		this.hostName = hostName;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public Class<? extends Controller> getController(String controllerName) throws ControllerException {
		Class<? extends Controller> controllerClass = getNativeController(controllerName);
		return controllerClass;
	}
	
	public Route getRoute(String segments) throws RouteException {
		Route route = getNativeRoute(segments);
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
		return new View(getNativeView(viewName));
	}
	
	public LibraryLoader getLibraryLoader() {
		return libraryLoader;
	}
	
	protected abstract Class<? extends Controller> getNativeController(String controllerName) throws ControllerException;
	protected abstract EvaluatedView getNativeView(String viewName) throws ViewException;
	protected abstract Collection<String> loadLibraries();
	protected abstract Route getNativeRoute(String segements) throws RouteException;
}
