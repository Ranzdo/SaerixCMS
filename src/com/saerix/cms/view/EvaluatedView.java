package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.saerix.cms.SaerixCMS;

public class EvaluatedView {
	private static GroovyShell groovyShell;
	
	private ArrayList<Script> tags = new ArrayList<Script>();
	private String content;
	
	public EvaluatedView(String viewName, String content) throws ViewException {
		try {
			if(groovyShell == null) {
				CompilerConfiguration compiler = new CompilerConfiguration();
				compiler.setScriptBaseClass("ViewBase");
				groovyShell = new GroovyShell(SaerixCMS.getGroovyClassLoader(), new Binding(), compiler);
			}
			
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
		catch(CompilationFailedException e) {
			throw (ViewException) new ViewException("Could not complie the view "+viewName).initCause(e);
		}
		catch(IOException e) {
			//Cannot happen
			e.printStackTrace();
		}
	}

	public ArrayList<Script> getTags() {
		return tags;
	}

	public String getContent() {
		return content;
	}
}
