package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Script;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.groovy.control.CompilerConfiguration;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.controller.Controller;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.ViewModel.ViewRow;

public class View {
	public static View getView(int hostId, String viewName) throws SQLException {
		ViewRow row = ((ViewModel)Database.getTable("views")).getView(hostId, viewName);
		return new View(row);
	}
	
	public static String mergeViews(Collection<View> views) {
		StringBuilder content = new StringBuilder();
		for(View view : views) {
			content.append(view.evaluate());
		}
		return content.toString();
	}
	
	private Map<String, Object> variables;
	private final ViewRow row;
	private Controller controller = null;
	
	private View(ViewRow row) {
		this.row = row;
	}
	
	public String evaluate() {
		Binding binding = new Binding();
		binding.setVariable("controller", controller);
		CompilerConfiguration compiler = new CompilerConfiguration();
		compiler.setScriptBaseClass("ViewBase");
		if(variables != null) {
			for(Entry<String, Object> var : variables.entrySet())
				binding.setVariable(var.getKey(), var.getValue());
		}
		
		GroovyShell shell = new GroovyShell(SaerixCMS.getGroovyClassLoader(), binding, compiler);
		
		StringBuilder evaluated = new StringBuilder();
		try {
			StringReader reader = new StringReader(row.getContent());
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
	        			if(bytee == -1)
	        				throw new ViewException("Unexpected end of view "+row.getName()+", missing end tag? ( } )");
	        			script.append((char)bytee);
	        		}
	        		if(script.toString().equals("literal")) {
	        			ignore = true;
	        		}
	        		else if(script.toString().equals("/literal")) {
	        			ignore = false;
	        		}
	        		else if(!ignore) {
	        			Object object = shell.evaluate("import com.saerix.cms.view.ViewBase;"+script.toString());
	        			if(object != null)
	        				evaluated.append(object.toString());
	        		}
	        		else {
	        			evaluated.append("{"+script.toString()+"}");
	        		}
				}
				else
					evaluated.append((char) bytee);
			}
			
			return evaluated.toString();
		}
		catch(IOException e) {
			//I don't if we should handle this Exception... I mean we are reading a string so how could it throw an IOException?
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

	public ViewRow getRow() {
		return row;
	}
}
