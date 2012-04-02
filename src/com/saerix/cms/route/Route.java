package com.saerix.cms.route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.RouteModel;
import com.saerix.cms.database.basemodels.RouteModel.RouteRow;
import com.saerix.cms.database.basemodels.RouteModel.RouteType;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.util.URLUtil;

public class Route {
	public static Route getRoute(int hostId, String segments) throws ControllerException, RouteException, IOException, SQLException {
		String[] segmentArray = URLUtil.splitSegments(segments);
		
		if(hostId == -1) {
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
					
					return new Route(hostId, route[0], route[1]);
				}
			}
			is.close();
		}
		else {
			RouteModel routes = (RouteModel) Database.getTable("routes");
			RouteRow row = routes.getRoute(hostId, segments);
			if(row != null) {
				if(row.getType() == RouteType.REDIRECT)
					return get302Route(row.getRouteValue());
				
				String[] route = URLUtil.splitSegments(row.getRouteValue());
				
				if(route.length != 2)
					throw new RouteException("Invalid route for "+segments+" on row "+row);
				
				return new Route(hostId, route[0], route[1]);
			}
		}
		
		//If we end up here there is no route for the segments, so we pick the default one. If that is not the case neither, then we send a 404 route
		if(segmentArray.length == 1 && segmentArray.length == 2)
			return get404Route();
		
		try {
			return new Route(hostId, segmentArray[0], segmentArray.length == 1 ? "index" : segmentArray[1]);
		} catch(RouteException e) {
			return get404Route();
		}
	}
	
	public static Route get404Route() {
		return new Route(new HTTP_404());
	}
	
	public static Route get302Route(String url) {
		return new Route(new HTTP_302(url));
	}
	
	public static Route get302Route(String hostName, String segments, Map<String,String> parameters, boolean secure) {
		return new Route(new HTTP_302(hostName, segments, parameters, secure));
	}

	private Class<? extends Controller> controller;
	private Method method;
	private Controller controllerObject;
	
	private Route(int hostId, String controllerName, String methodName) throws RouteException {
		try {
			this.controller = Controller.getController(hostId, controllerName);
			this.method = controller.getMethod(methodName);
		} catch(NoSuchMethodException e) {
			throw new RouteException("The method "+methodName+" does not exists in the controller "+controllerName+".");
		} catch(SecurityException e) {
			throw new RouteException("The method "+methodName+" is not accessible in the controller "+controllerName+".");
		} catch (ControllerException e) {
			throw new RouteException("The controller "+controllerName+" does not exist.");
		}
	}
	
	Route(Controller controller) {
		this.controllerObject = controller;
	}
	
	public Controller invokeRoute(PageLoadEvent pageLoadEvent) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(controllerObject != null)
			return controllerObject;
			
		return Controller.invokeController(controller, method, pageLoadEvent);
	}
}
