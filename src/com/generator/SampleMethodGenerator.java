package com.generator;

public class SampleMethodGenerator implements ICodeGenerator {

	@Override
	public String generate(Object o) {
		String method="public String sayHello2(String name){return \"Hello \"+name;}";
		return method;
	}

}
