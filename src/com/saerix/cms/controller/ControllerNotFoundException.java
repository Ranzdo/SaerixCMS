package com.saerix.cms.controller;

public class ControllerNotFoundException extends ControllerException {
	private static final long serialVersionUID = 1L;
	
	public ControllerNotFoundException(String controllerName) {
		super("The controller \""+controllerName+"\" could not be found.");
	}
}

