package com.saerix.cms.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import com.saerix.cms.database.Model.Row;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableConfig {
	String name();

	boolean persistent() default false;

	Class<? extends Row> rowclass() default Row.class;
}
