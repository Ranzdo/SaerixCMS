package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.groovy.control.CompilerConfiguration;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.ViewModel.ViewRow;
import com.saerix.cms.util.Util;

public class View {
	private static GroovyShell groovyShell;
	
	private static Map<String, EvaluatedView> cachedViews = Collections.synchronizedMap(new HashMap<String, EvaluatedView>());
	
	public static View getView(int hostId, String viewName) throws SQLException, IOException {
		if(groovyShell == null) {
			CompilerConfiguration compiler = new CompilerConfiguration();
			compiler.setScriptBaseClass("ViewBase");
			groovyShell = new GroovyShell(SaerixCMS.getGroovyClassLoader(), new Binding(), compiler);
		}
		
		EvaluatedView view = cachedViews.get(hostId+viewName);
		if(view == null)
			return reloadView(hostId, viewName);
		
		return new View(view);
	}
	
	public static View reloadView(int hostId, String viewName) throws SQLException, IOException {
		if(groovyShell == null) {
			CompilerConfiguration compiler = new CompilerConfiguration();
			compiler.setScriptBaseClass("ViewBase");
			groovyShell = new GroovyShell(SaerixCMS.getGroovyClassLoader(), new Binding(), compiler);
		}
		EvaluatedView view = null;
		if(hostId == -1) {
			String res = "/com/saerix/cms/cms/views/"+viewName+".html";
			if(!Util.resourceExists(res))
				throw new ViewException("Could not find the local view "+viewName);
			
			view = new EvaluatedView(viewName, Util.readResource(res));
		}
		else {
			ViewRow row = ((ViewModel)Database.getTable("views")).getView(hostId, viewName);
			if(row == null)
				throw new ViewException("Could not find the view "+viewName);
			
			view = new EvaluatedView(row.getName(), row.getContent());
		}
		
		synchronized (cachedViews) {
			cachedViews.put(hostId+viewName, view);
		}
		
		return new View(view);
	}
	
	public static String mergeViews(Collection<View> views) {
		StringBuilder content = new StringBuilder();
		for(View view : views) {
			content.append(view.evaluate());
		}
		return content.toString();
	}
	
	private static class EvaluatedView {
		private final ArrayList<Script> tags = new ArrayList<Script>();
		private final String content;
		
		private EvaluatedView(String viewName, String content) throws IOException {
			StringBuilder evaluated = new StringBuilder();
			StringReader reader = new StringReader(content);
			int bytee;
			boolean ignore = false;
			while((bytee = reader.read()) != -1) {
				if(bytee == 123) {
		    		StringBuilder script = new StringBuilder();
		    		int step = 0;
	        		while((bytee = reader.read()) != 125 || step != 0) {
	        			if(bytee == 123)
	        				step++;
	        			if(bytee == 125)
	        				step--;
	        			if(bytee == -1 && !ignore)
	        				throw new ViewException("Unexpected end of view "+viewName+", missing end tag? ( } )");
	        			script.append((char)bytee);
	        		}
	        		if(script.toString().equals("literal")) {
	        			ignore = true;
	        		}
	        		else if(script.toString().equals("/literal")) {
	        			ignore = false;
	        		}
	        		else if(!ignore) {
	        			try{
		        			tags.add(groovyShell.parse("import com.saerix.cms.view.ViewBase;"+script.toString()));
		        			evaluated.append("{Script:"+tags.size()+"}");
	        			}
	        			catch(Exception e) {
	        				evaluated.append("<span style=\"color:red;\">"+e.getMessage()+"</span>");
	        			}
	        		}
	        		else {
	        			evaluated.append("{"+script.toString()+"}");
	        		}
				}
				else
					evaluated.append((char) bytee);
			}
			this.content = evaluated.toString();
		}
		
	}
	
	private EvaluatedView evalView;
	private String content;
	private Map<String, Object> variables;
	private Controller controller = null;
	
	private View(EvaluatedView evalView) {
		this.evalView = evalView;
	}
	
	public View(String content) {
		this.content = content;
	}
	
	public String evaluate() {
		if(content != null)
			return content;
		
		String content = evalView.content;
		
		Binding binding = new Binding();
		binding.setVariable("controller", controller);
		
		if(variables != null) {
			for(Entry<String, Object> var : variables.entrySet())
				binding.setVariable(var.getKey(), var.getValue());
		}
		
		for(int i = 1; i <= evalView.tags.size();i++) {
			Script script = evalView.tags.get(i-1);
			script.setBinding(binding);
			Object object;
			try{
				object = script.run();
			}
			catch(Exception e) {
				object = e.getMessage();
			}
			if(object != null) {
				content = content.replace("{Script:"+i+"}", object.toString());
			}
		}
		
		try {
			return new String(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
}
