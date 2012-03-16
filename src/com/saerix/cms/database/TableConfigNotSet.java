package com.saerix.cms.database;

public class TableConfigNotSet extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TableConfigNotSet(Class<? extends Model> clazz) {
		super("TableConfig annotation not set in the class "+clazz.getName());
	}
}
