package com.saerix.cms.database;

public class TableHasNoPrimaryKeys extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TableHasNoPrimaryKeys(String tableName) {
		super("The table "+tableName+" has no primary keys. It's model will not be loaded.");
	}
}
