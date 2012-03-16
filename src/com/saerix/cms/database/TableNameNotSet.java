package com.saerix.cms.database;

public class TableNameNotSet extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TableNameNotSet(Class<? extends Model> tableClass) {
		super("The table name is not set for the table model "+tableClass.getName());
	}
}
