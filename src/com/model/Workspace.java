package com.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Workspace {
	@XmlElement
	List<Project> project;

	public List<Project> getProject() {
		return project;
	}
	public void setProjects(List<Project> project) {
		this.project = project;
	}


}
