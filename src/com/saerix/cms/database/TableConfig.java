package com.saerix.cms.database;

public @interface TableConfig {
	String name();
	boolean persistent() default false;
	Class<? extends Row> rowclass() default Row.class;
}
