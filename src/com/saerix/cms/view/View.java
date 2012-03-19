package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ViewModel;
import com.saerix.cms.database.basemodels.ViewModel.ViewRow;

public class View {
	public static View getView(String viewName) throws SQLException {
		ViewRow row = ((ViewModel)Database.getTable("views")).getView(viewName);
		return new View(row);
	}
	
	private Map<String, Object> variables;
	private final ViewRow row;
	private final String content;
	
	private View(ViewRow row) {
		this.row = row;
		content = row.getContent();
	}
	
	public String evaluate() {
		Binding binding = new Binding();
		if(variables != null) {
			for(Entry<String, Object> var : variables.entrySet())
				binding.setVariable(var.getKey(), var.getValue());
		}
		
		GroovyShell shell = new GroovyShell(SaerixCMS.getGroovyClassLoader(), binding);
		
		StringBuilder evaluated = new StringBuilder();
		try {
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
	        			if(bytee == -1)
	        				throw new ViewException("Unexpected end of view, missing end tag? ( } )");
	        			script.append((char)bytee);
	        		}
	        		if(script.toString().equals("literal")) {
	        			ignore = true;
	        		}
	        		else if(script.toString().equals("/literal")) {
	        			ignore = false;
	        		}
	        		else if(!ignore) {
	        			Object object = shell.evaluate(script.toString());
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
			//I don't if we should handle this Exception... I mean we are reading a string so how could it be an IOException?
			e.printStackTrace();
			return null;
		}
	}
	
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public ViewRow getRow() {
		return row;
	}
}
