package com.favorsoft.helper.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.quartz.SchedulerException;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;

public interface HelperService {
	public Helper getHelper(String knoxId);
	
	public void saveProject(Project project) throws ClassNotFoundException, SchedulerException;
	
	public void closeProject(Project project);

	public void saveProjectShift(ProjectShift projectShift);
	
	public void updateProjectShift(ProjectShift oldProjectShift, ProjectShift newProjectShift);
	
	public void saveShiftHelperRequest(ShiftHelperRequest shiftHelperRequest);
	
	public Project getProjectByProjectName(String projectName);
	
	public Optional<Project> getProject(String id);

	public List<Project> getProjectList(boolean active);
	
	public Helper updateHelper(Helper oldHelper, Helper newHelper);
	
	public Helper saveHelper(Helper helper);

	public void deleteProjectHelper(Project project, Helper helper);

	public Project updateProject(Project oldProject, Project newProject);
	
	public List<ProjectShift> getProjectShiftList(String projectId, boolean isOpen);
	
	public List<ProjectShift> getProjectShiftBetweenHelpDate(Project project, Date startDate, Date endDate);
	
	public ProjectShift getProjectShift(Project project, Date helpDate);

	public void handupHelper(ProjectShift projectShift) throws Exception;

	public void handdownHelper(ProjectShift projectShift) throws Exception;
}
