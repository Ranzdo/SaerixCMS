package com.saerix.cms.route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.controller.ControllerException;
import com.saerix.cms.host.Host;
import com.saerix.cms.libapi.events.PageLoadEvent;

public class Route {
	
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
	private PageLoadEvent event;
	
	public Route(Host host, String controllerName, String methodName, PageLoadEvent event) throws RouteException {
		this.event = event;
		try {
			this.controller = host.getController(controllerName);
			this.method = controller.getMethod(methodName);
		} catch(NoSuchMethodException e) {
			throw new RouteException("The method "+methodName+" does not exists in the controller "+controllerName+".");
		} catch(SecurityException e) {
			this.controllerObject = new HTTP_404();
		} catch (ControllerException e) {
			throw new RouteException("The controller "+controllerName+" does not exist.");
		}
	}
	
	Route(Controller controller) {
		this.controllerObject = controller;
	}
	
	public Controller invokeRoute() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(controllerObject != null)
			return controllerObject;
			
		return Controller.invokeController(controller, method, event);
	}
}
