package com.saerix.cms.database;

public class InvalidSuperClass extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidSuperClass(Class<?> was, Class<?> shouldBe, Class<?> clazz) {
		super("The class "+clazz.getName()+" had the superclass "+was.getName()+". It needs to be "+shouldBe.getName());
	}

}
