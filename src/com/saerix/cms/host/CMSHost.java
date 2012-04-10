package com.saerix.cms.host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.controller.ControllerNotFoundException;
import com.saerix.cms.libapi.LibraryException;
import com.saerix.cms.route.Route;
import com.saerix.cms.route.RouteException;
import com.saerix.cms.util.URLUtil;
import com.saerix.cms.util.Util;
import com.saerix.cms.view.EvaluatedView;
import com.saerix.cms.view.ViewException;

public class CMSHost extends Host {

	public CMSHost(SaerixHttpServer server, String hostName) throws LibraryException {
		super(server, hostName);
	}
	
	private Map<String, EvaluatedView> loadedViews = new HashMap<String, EvaluatedView>();
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Controller> getHostController(String controllerName) throws ControllerException {
		try {
			return (Class<? extends Controller>) Class.forName("com.saerix.cms.cms.controllers."+controllerName);
		} catch (ClassNotFoundException e) {
			throw new ControllerNotFoundException(controllerName);
		} catch(ClassCastException e) {
			throw (ControllerException) new ControllerException().initCause(e);
		}
	}

	@Override
	public EvaluatedView getHostView(String viewName) throws ViewException {
		try {
			String res = "/com/saerix/cms/cms/views/"+viewName+".html";
			if(!Util.resourceExists(res))
				throw new ViewException("Could not find the local view "+viewName);
			
			return new EvaluatedView(getServer().getInstance().getGroovyClassLoader(), viewName, Util.readResource(res));
		}
		catch(IOException e) {
			throw (ViewException) new ViewException().initCause(e);
		}
	}

	@Override
	public Collection<String> loadLibraries() {
		//We should always only include the base libs
		return new ArrayList<String>();
	}

	@Override
	public Route getHostRoute(String segments) throws RouteException {
		try {
			String[] segmentArray = URLUtil.splitSegments(segments);
			InputStream is = Route.class.getResourceAsStream("/com/saerix/cms/cms/routes");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null)   {
				String[] values = line.split("=");
				if(Arrays.equals(segmentArray, URLUtil.splitSegments(values[0].trim()))) {
					String[] route = URLUtil.splitSegments(values[1]);
					is.close();
					
					if(route.length != 2)
						throw new RouteException("Invalid route for "+segments+" (local).");
					
					return new Route(this, route[0], route[1]);
				}
			}
			is.close();
		}
		catch(IOException e) {
			throw (RouteException) new RouteException().initCause(e);
		}
		
		return null;
	}
}
