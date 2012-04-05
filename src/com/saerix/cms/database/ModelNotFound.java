package com.saerix.cms.database;

public class ModelNotFound extends DatabaseException {
	private static final long serialVersionUID = 1L;
	
	public ModelNotFound(String tableName) {
		super("Did not find a model to the table \""+tableName+"\"");
	}
}
