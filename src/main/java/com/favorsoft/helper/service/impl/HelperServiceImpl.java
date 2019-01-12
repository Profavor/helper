package com.favorsoft.helper.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;
import com.favorsoft.helper.repository.HelperRepository;
import com.favorsoft.helper.repository.ProjectRepository;
import com.favorsoft.helper.repository.ProjectShiftRepository;
import com.favorsoft.helper.repository.ShiftHelperRequestRepository;
import com.favorsoft.helper.service.HelperService;

@Service
public class HelperServiceImpl implements HelperService{
	private final String PROJECT_STATUS_OPEN = "OPEN";
	
	@Autowired
	private HelperRepository helperRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectShiftRepository projectShiftRepository;
	
	@Autowired
	private ShiftHelperRequestRepository shiftHelperRequestRepositry;

	@Override
	public Helper getHelper(String knoxId) {
		return helperRepository.findByKnoxId(knoxId);
	}

	@Override
	public void saveProject(Project project) {
		projectRepository.save(project);		
	}
	
	@Override
	public Project getProjectByProjectName(String projectName) {
		return projectRepository.findByProjectName(projectName);
	}
	
	@Override
	public List<Project> getProjectList(boolean active){
		List<Project> list = null;
		if(active) {
			list = projectRepository.findByStatus(PROJECT_STATUS_OPEN);
		}else {
			list = projectRepository.findAll();
		}
		return list;
	}

	@Override
	public void saveProjectShift(ProjectShift projectShift) {		
		Project project = getProject(projectShift.getProjectId()).get();
		projectShift.setProject(project);		
		projectShiftRepository.save(projectShift);		
	}

	@Override
	public void saveShiftHelperRequest(ShiftHelperRequest shiftHelperRequest) {
		shiftHelperRequestRepositry.save(shiftHelperRequest);		
	}

	@Override
	public Optional<Project> getProject(String id) {
		return projectRepository.findById(id);
	}
	
	@Override
	public Helper updateHelper(Helper oldHelper, Helper newHelper){
		oldHelper.setUserName(newHelper.getUserName());
		oldHelper.setBirthday(newHelper.getBirthday());
		oldHelper.setPhone(newHelper.getPhone());
		oldHelper.setSite1365(newHelper.getSite1365());
		oldHelper.setEnable(newHelper.isEnable());
		return helperRepository.saveAndFlush(oldHelper);
	}

	@Override
	public Helper saveHelper(Helper helper) {
		return helperRepository.saveAndFlush(helper);
	}

	@Override
	public Project updateProject(Project oldProject, Project newProject) {
		oldProject.setHelpers(newProject.getHelpers());
		oldProject.setDescription(newProject.getDescription());
		oldProject.setEndDate(newProject.getEndDate());
		oldProject.setStartDate(newProject.getStartDate());
		oldProject.setProjectName(newProject.getProjectName());
		oldProject.setStatus(newProject.getStatus());		
		return projectRepository.saveAndFlush(oldProject);
	}
	
	@Override
	public void deleteProjectHelper(Project project, Helper helper) {	
		List<Helper> helperList = project.getHelpers();
		for(int i=0; i<helperList.size(); i++) {
			Helper tempHelper = helperList.get(i);
			
			if(tempHelper.getKnoxId().equals(helper.getKnoxId())) {
				helperList.remove(i);
			}			
		}
		
		project.setHelpers(helperList);		
		projectRepository.save(project);
	}

	@Override
	public void closeProject(Project project) {
		project.setStatus("CLOSE");
		projectRepository.save(project);		
	}

	@Override
	public List<ProjectShift> getProjectShiftList() {
		return projectShiftRepository.findAll();
	}
}
