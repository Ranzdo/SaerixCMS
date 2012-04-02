package com.saerix.cms.host;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.HostModel.HostRow;
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
	private static Map<String, Host> loadedHosts = Collections.synchronizedMap(new HashMap<String, Host>());
	
	private static CMSHost cmsHost;
	
	public static Host getHost(String hostName) throws HostException {
		Host host = loadedHosts.get(hostName);
		if(host != null)
			return host;
		
		try {
			try {
				HostRow row = (HostRow) ((HostModel) Database.getTable("hosts")).getHost(hostName);
				if(row != null) {
					host = new DatabaseHost(row.getId(), hostName);
					loadedHosts.put(hostName, host);
					return host;
				}
			}
			catch(SQLException e) {
				throw (HostException) new HostException().initCause(e);
			}
		
		if(cmsHost == null)
			cmsHost = new CMSHost(SaerixCMS.getProperties().getProperty("cms_hostname"));
		
		return cmsHost;
		
		}
		catch(LibraryException e) {
			throw (HostException) new HostException().initCause(e);
		}
	}
	
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
	
	public abstract Class<? extends Controller> getNativeController(String controllerName) throws ControllerException;
	public abstract EvaluatedView getNativeView(String viewName) throws ViewException;
	public abstract Collection<String> loadLibraries();
	public abstract Route getNativeRoute(String segements) throws RouteException;
}
