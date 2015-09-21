package com.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JavaFile {
	@XmlAttribute
	String src;
	@XmlAttribute(name="post-package")
	String postPackage;
	@XmlAttribute
	String name;
	@XmlElement(name="method")
	List<String> methods;
	@XmlElement(name="field")
	List<String> fields;
	public String getSrc() {
		return src;
	}


	public void setSrc(String src) {
		this.src = src;
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
	
	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}
	
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

}
