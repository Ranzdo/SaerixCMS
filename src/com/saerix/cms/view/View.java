package com.saerix.cms.view;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.saerix.cms.controller.Controller;

public class View {
	public static String mergeViews(Collection<View> views) {
		StringBuilder content = new StringBuilder();
		for(View view : views) {
			content.append(view.evaluate());
		}
		return content.toString();
	}
	
	private EvaluatedView evalView;
	private String content;
	private Map<String, Object> variables;
	private Controller controller = null;
	
	public View(EvaluatedView evalView) {
		this.evalView = evalView;
	}
	
	public View(String content) {
		this.content = content;
	}
	
	public String evaluate() {
		if(content != null)
			return content;
		
		StringBuffer content = new StringBuffer(evalView.getContent());
		
		Binding binding = new Binding();
		binding.setVariable("controller", controller);
		
		if(variables != null) {
			for(Entry<String, Object> var : variables.entrySet())
				binding.setVariable(var.getKey(), var.getValue());
		}
		
		for(Entry<Integer, Script> entry : evalView.getTags().entrySet()) {
			Script script = entry.getValue();
			Object object;
			synchronized(script) {
				script.setBinding(binding);
				try{
					object = script.run();
				}
				catch(Exception e) {
					object = e.getMessage();
				}
			}
			if(object != null) {
				content.insert(entry.getKey(), object.toString());
			}
		}
		
		return content.toString();
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
