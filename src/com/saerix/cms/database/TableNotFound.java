package com.saerix.cms.database;

public class TableNotFound extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TableNotFound(String tableName) {
		super("Table not found or does not have an model to attach to.");
	}
}
