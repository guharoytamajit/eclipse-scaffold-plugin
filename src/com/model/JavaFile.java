package com.model;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JavaFile {
	@XmlAttribute
	String source;
	@XmlAttribute(name="post-package")
	String postPackage;
	@XmlAttribute
	String name;
	@XmlElement(name="method")
	List<JavaMethod> methods;
	@XmlElement(name="field")
	List<JavaField> fields;
	@XmlElement(name="import")
	Set<String> imports;
	@XmlAttribute
	String generator;
	@XmlAttribute
	String copy;
	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}
	public String getPostPackage() {
		return postPackage;
	}

	
	public void setPostPackage(String postPackage) {
		this.postPackage = postPackage;
	}
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<JavaMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethod> methods) {
		this.methods = methods;
	}
	
	public List<JavaField> getFields() {
		return fields;
	}

	public void setFields(List<JavaField> fields) {
		this.fields = fields;
	}


	public String getGenerator() {
		return generator;
	}


	public void setGenerator(String generator) {
		this.generator = generator;
	}


	public String getCopy() {
		return copy;
	}


	public void setCopy(String copy) {
		this.copy = copy;
	}


	public Set<String> getImports() {
		return imports;
	}


	public void setImports(Set<String> imports) {
		this.imports = imports;
	}

}
