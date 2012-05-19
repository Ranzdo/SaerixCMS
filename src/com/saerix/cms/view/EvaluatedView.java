package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

public class EvaluatedView {
	private static GroovyShell groovyShell;
	
	private LinkedHashMap<Integer, Script> tags = new LinkedHashMap<Integer, Script>();
	private String rawcontent;
	private String content;
	
	public EvaluatedView(GroovyClassLoader parent, String viewName, String content) throws ViewException {
		this.rawcontent = content;
		try {
			if(groovyShell == null) {
				CompilerConfiguration compiler = new CompilerConfiguration();
				compiler.setScriptBaseClass("ViewBase");
				groovyShell = new GroovyShell(parent, new Binding(), compiler);
			}
			
			StringBuilder evaluated = new StringBuilder();
			StringReader reader = new StringReader(rawcontent);
			int bytee;
			int place = 0;
			while((bytee = reader.read()) != -1) {
				if(bytee == 123) {
					if((bytee = reader.read()) == 123) {
			    		StringBuilder script = new StringBuilder();
			    		int step = 0;
		        		while(true) {
		        			bytee = reader.read();
		        			if(step == 0 && bytee == 125) {
		        				bytee = reader.read();
		        				if(bytee == 125)
		        					break;
		        				else {
		        					script.append("}"+(char)bytee);
		        				}
		        			}
		        			if(bytee == 123)
		        				step++;
		        			if(bytee == 125)
		        				step--;
		        			if(bytee == -1)
		        				throw new ViewException("Unexpected end of view "+viewName+", missing end tag? ( }} )");
		        			
		        			script.append((char)bytee);
		        		}
	        			try{
		        			tags.put(place, groovyShell.parse("import com.saerix.cms.view.ViewBase;"+script.toString()));
	        			}
	        			catch(Exception e) {
	        				String error = "<span style=\"color:red;\">"+e.getMessage()+"</span>";
	        				place += error.length();
	        				evaluated.append(error);
	        			}
					}
					else {
						evaluated.append("{"+(char) bytee);
						place += 2;
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
	
	public String getRawContent() {
		return rawcontent;
	}
}
