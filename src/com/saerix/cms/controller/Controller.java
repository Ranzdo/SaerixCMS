package com.saerix.cms.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.host.Host;
import com.saerix.cms.libapi.Library;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.sessionlib.Session;
import com.saerix.cms.sessionlib.SessionLibrary;
import com.saerix.cms.util.HttpError;
import com.saerix.cms.view.View;
import com.saerix.cms.view.ViewException;
import com.saerix.cms.view.ViewNotFoundException;

public class Controller {
	
	public static Controller invokeController(Class<? extends Controller> controllerClass, Method method, PageLoadEvent pageLoadEvent) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(controllerClass == null)
			throw new NullPointerException("The field controllerClass can not be null.");
		
		if(method == null)
			throw new NullPointerException("The field method can not be null.");
		
		if(pageLoadEvent == null)
			throw new NullPointerException("The field parameters can not be null.");
		
		Controller controller = controllerClass.newInstance();
		controller.event = pageLoadEvent;
		if(controller.onload()) {
			method.invoke(controller);
		}
		return controller;
	}
	
	private PageLoadEvent event;
	private Map<String, Object> passedVars = null;
	private ArrayList<View> views = new ArrayList<View>();
	private int returnCode = 200;
	
	public void set(PageLoadEvent event) {
		if(event == null)
			this.event = event;
	}
	
	public void view(String viewName, Map<String, Object> variables) throws ViewNotFoundException, ViewException {
		View view = event.getHost().getView(viewName);
		view.setController(this);
		view.setVariables(variables);
		views.add(view);
	}
	
	public void view(String viewName) throws ViewNotFoundException, ViewException {
		view(viewName, null);
	}
	
	public void echo(Object echo) {
		if(echo == null)
			views.add(new View("null"));
		else
			views.add(new View(echo.toString()));
	}
	
	public List<View> getViews() {
		return views;
	}
	
	public PageLoadEvent getPageLoadEvent() {
		return event;
	}
	
	public String getHostName() {
		return event.getHost().getHostName();
	}
	
	public String segment(int index) {
		try {
			return event.getSegments()[index];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String post(String parameter, String deafult) {
		List<String> list = event.getPostParameters().get(parameter);
		
		return list == null ? deafult : list.size() < 1 ? deafult : list.get(0);
	}
	
	public String post(String parameter) {
		return post(parameter, "");
	}
	
	public String get(String parameter, String deafult) {
		List<String> list = event.getGetParameters().get(parameter);
		
		return list == null ? deafult : list.size() < 1 ? deafult : list.get(0);
	}
	
	public String get(String parameter) {
		return get(parameter, "");
	}
	
	public Database database() {
		return getPageLoadEvent().getHost().getServer().getInstance().getDatabaseLoader().getMainDatabase();
	}
	
	public Model model(String tableName) throws DatabaseException {
		return getPageLoadEvent().getHost().getServer().getInstance().getDatabaseLoader().getMainDatabase().getModel(tableName);
	}
	
	public Model model(String databaseName, String tableName) throws DatabaseException {
		return getPageLoadEvent().getHost().getServer().getInstance().getDatabaseLoader().getDatabase(databaseName).getModel(tableName);
	}
	
	public Object getPassedVariable(String variableName) {
		return passedVars == null ? null : passedVars.get(variableName);
	}
	
	public void setPassedVariables(Map<String, Object> vars) {
		passedVars = vars;
	}
	
	public void redirect(String segments, Map<String, String> para) {
		returnCode = 302;
		getPageLoadEvent().getHandle().getResponseHeaders().add("Location", event.getHost().getURL(segments, para, event.isSecure()));
	}
	
	public void redirect(String url, boolean local) {
		returnCode = 302;
		if(local)
			redirect(url, null);
		else
			getPageLoadEvent().getHandle().getResponseHeaders().add("Location", url);
	}
	
	public void redirect(String segements) {
		redirect(segements, true);
	}
	
	public String base_url() {
		return event.getHost().getURL( "", null, event.isSecure());
	}
	
	public Library lib(String libName) {
		return event.getHost().getLibraryLoader().getLib(libName);
	}
	
	public Session session() {
		return ((SessionLibrary)lib("session")).session();
	}
	
	public void show_404() {
		returnCode = 404;
		echo(HttpError.RETURN_404);
	}
	
	public boolean onload() {
		return true;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	
	public void setMime(String mime) {
		getPageLoadEvent().getHandle().getResponseHeaders().remove("Content-Type");
		getPageLoadEvent().getHandle().getResponseHeaders().add("Content-Type", mime+"; charset=utf-8");
	}

	public Host getHost() {
		return event.getHost();
	}
}
