package com.saerix.cms.view;

public class ViewNotFoundException extends ViewException {
	private static final long serialVersionUID = 1L;

	public ViewNotFoundException(String viewName) {
		super("The view "+viewName+" was not found.");
	}
}
