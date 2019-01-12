package com.favorsoft.helper.entity.key;

import java.io.Serializable;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;

public class ProjectHelperKey implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Project project;
    private Helper helper;
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Helper getHelper() {
		return helper;
	}
	public void setHelper(Helper helper) {
		this.helper = helper;
	}
    
	
    
    
}