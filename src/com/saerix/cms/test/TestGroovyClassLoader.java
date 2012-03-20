package com.saerix.cms.test;

import groovy.lang.GroovyClassLoader;

public class TestGroovyClassLoader {
	public static void main(String[] args) throws ClassNotFoundException {
		GroovyClassLoader gClassLoader = new GroovyClassLoader(TestGroovyClassLoader.class.getClassLoader());
		System.out.print(Class.forName("controllers.test").getSuperclass());
	}
}
