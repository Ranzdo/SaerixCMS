package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

public class EvaluatedView {
	private static GroovyShell groovyShell;
	
	private LinkedHashMap<Integer, Script> tags = new LinkedHashMap<Integer, Script>();
	private String content;
	
	public EvaluatedView(GroovyClassLoader parent, String viewName, String content) throws ViewException {
		try {
			if(groovyShell == null) {
				CompilerConfiguration compiler = new CompilerConfiguration();
				compiler.setScriptBaseClass("ViewBase");
				groovyShell = new GroovyShell(parent, new Binding(), compiler);
			}
			
			StringBuilder evaluated = new StringBuilder();
			StringReader reader = new StringReader(content);
			int bytee;
			int place = 0;
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
		        			tags.put(place, groovyShell.parse("import com.saerix.cms.view.ViewBase;"+script.toString()));
		    				place++;
	        			}
	        			catch(Exception e) {
	        				String error = "<span style=\"color:red;\">"+e.getMessage()+"</span>";
	        				place += error.length();
	        				evaluated.append(error);
	        			}
	        		}
	        		else {
	        			String noscript = "{"+script.toString()+"}";
	        			place += noscript.length();
	        			evaluated.append(noscript);
	        		}
				}
				else {
					evaluated.append((char) bytee);
					place++;
				}
				
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

	public Map<Integer, Script> getTags() {
		return tags;
	}

	public String getContent() {
		return content;
	}
}
