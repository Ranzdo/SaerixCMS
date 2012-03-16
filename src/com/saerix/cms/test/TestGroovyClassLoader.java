package com.saerix.cms.test;

import groovy.lang.GroovyClassLoader;

public class TestGroovyClassLoader {
	public static void main(String[] args) throws ClassNotFoundException {
		GroovyClassLoader gClassLoader = new GroovyClassLoader(TestGroovyClassLoader.class.getClassLoader());
		Class<?> clazz = gClassLoader.parseClass("package templates; class TheLol { def lol() {println \":D\"}}");
		System.out.print(clazz.getName());
	}
}
