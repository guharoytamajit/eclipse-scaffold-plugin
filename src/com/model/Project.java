package com.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {
	@XmlAttribute
	String name;
	@XmlElement(name="javafile")
	List<JavaFile> javaFile;
	
	@XmlElement
	List<String> src;

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}


	public List<JavaFile> getJavaFile() {
		return javaFile;
	}


	public void setJavaFile(List<JavaFile> javaFile) {
		this.javaFile = javaFile;
	}


	public List<String> getSrc() {
		return src;
	}


	public void setSrc(List<String> src) {
		this.src = src;
	}

	
}
