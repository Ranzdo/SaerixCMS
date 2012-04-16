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
		
		String content = evalView.getContent();
		
		Binding binding = new Binding();
		binding.setVariable("controller", controller);
		
		if(variables != null) {
			for(Entry<String, Object> var : variables.entrySet())
				binding.setVariable(var.getKey(), var.getValue());
		}
		
		for(int i = 1; i <= evalView.getTags().size();i++) {
			Script script = evalView.getTags().get(i-1);
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
		
		return content;
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
