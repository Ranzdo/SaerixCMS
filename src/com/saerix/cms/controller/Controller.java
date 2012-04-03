package com.saerix.cms.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.Model;
import com.saerix.cms.host.Host;
import com.saerix.cms.libapi.Library;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.saerix.cms.sessionlib.Session;
import com.saerix.cms.sessionlib.SessionLibrary;
import com.saerix.cms.util.URLUtil;
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
	
	private String redirect = null;
	private PageLoadEvent event;
	private Map<String, Object> passedVars = null;
	private ArrayList<View> views = new ArrayList<View>();
	private int returnCode = 200;
	
	public Controller() {
		
	}
	
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
	
	public void echo(String echo) {
		views.add(new View(echo));
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
	
	public String post(String parameter) {
		List<String> list = event.getPostParameters().get(parameter);
		
		return list == null ? "" : list.size() < 1 ? "" : list.get(0);
	}
	
	public String get(String parameter) {
		List<String> list = event.getGetParameters().get(parameter);
		
		return list == null ? "" : list.size() < 1 ? "" : list.get(0);
	}
	
	public Model model(String tableName) {
		return Database.getTable(tableName);
	}
	
	public Object getPassedVariable(String variableName) {
		return passedVars == null ? null : passedVars.get(variableName);
	}
	
	public void setPassedVariables(Map<String, Object> vars) {
		passedVars = vars;
	}
	
	public void redirect(String segments, Map<String, String> para) {
		returnCode = 302;
		redirect = URLUtil.getURL(getHostName(), segments, para, event.isSecure());
	}
	
	public void redirect(String url, boolean local) {
		returnCode = 302;
		if(local)
			redirect = URLUtil.getURL(getHostName(), url, null, event.isSecure());
		else
			redirect = url;
	}
	
	public void redirect(String segements) {
		redirect(segements, true);
	}
	
	public String redirectTo() {
		return redirect;
	}
	
	public String base_url() {
		return URLUtil.getURL(getHostName(), "", null, event.isSecure());
	}
	
	public Library lib(String libName) {
		return event.getHost().getLibraryLoader().getLib(libName);
	}
	
	public Session session() {
		return ((SessionLibrary)lib("session")).session();
	}
	
	public void show_404() {
		returnCode = 404;
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

	public Host getHost() {
		return event.getHost();
	}
}
