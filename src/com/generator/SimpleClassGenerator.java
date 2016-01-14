package com.generator;

public class SimpleClassGenerator implements ICodeGenerator {

	@Override
	public String generate(Object o) {
		String clazz="public class Demo{}";
		return clazz;
	}

}
