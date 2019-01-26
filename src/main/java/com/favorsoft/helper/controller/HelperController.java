package com.favorsoft.helper.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.shared.model.DropdownModel;
import com.favorsoft.shared.model.ResponseModel;

@RestController
@RequestMapping("/api/helper")
public class HelperController {
	@Autowired
	private HelperService helperService;
	
	@RequestMapping(value="/validCronExpression")
	public boolean validCronExpression(@RequestParam String triggerValue) {
		return CronExpression.isValidExpression(triggerValue);
	}
	
	@RequestMapping("/getProjectList")
	public List<Project> getProjectList(){		
		List<Project> list = helperService.getProjectList(false);
		return list;
	}
	
	
	@RequestMapping("/getProjectShiftList")
	public List<ProjectShift> getProjectShiftList(@RequestParam String projectId, @RequestParam String projectStatus){		
		
		List<ProjectShift> list = helperService.getProjectShiftList(projectId, projectStatus);
		return list.stream().sorted(Comparator.comparing(ProjectShift::getHelpDate)).collect(Collectors.toList());
		
	}
	
	@RequestMapping("/getProjectShiftListByKnoxId")
	public List<ProjectShift> getProjectShiftListByKnoxId(@RequestParam String knoxId){	
		List<ProjectShift> list = helperService.getProjectShiftListByKnoxId(knoxId);
		return list.stream().sorted(Comparator.comparing(ProjectShift::getHelpDate).reversed()).collect(Collectors.toList());		
	}
	
	@RequestMapping("/project/list/dropdown")
	public ResponseModel getProjectListDropdown(HttpServletRequest request){
		
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

		ResponseModel res = new ResponseModel();
		
		List<Project> projectList = helperService.getProjectList(true);
		List<DropdownModel> dropdownModelList = new ArrayList<DropdownModel>();
		int check = 0;
		for(Project project: projectList) {
			boolean flag = false;
			for(Helper tempHelper: project.getHelpers()) {
				if(auth.getName().equals(tempHelper.getKnoxId())){					
					flag = true;
					check++;
					break;
				}
			}
			if(flag && check == 1) {
				dropdownModelList.add(new DropdownModel(project.getProjectName(), project.getId(), true));
			}else {
				dropdownModelList.add(new DropdownModel(project.getProjectName(), project.getId(), false));
			}			
		}
		res.setSuccess(true);		
		res.setResults(dropdownModelList);		
		return res;
	}
	
	@RequestMapping(value="/project/shift/save", method=RequestMethod.POST)
	public ResponseModel createProjectShift(@RequestBody ProjectShift projectShift) {
		ResponseModel res = new ResponseModel();
		try {
			helperService.saveProjectShift(projectShift);
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}		
		return res;
	}
	
	@RequestMapping(value="/project/shift/request/handup", method=RequestMethod.POST)
	public ResponseModel handupHelper(@RequestBody ProjectShift projectShift) {
		ResponseModel res = new ResponseModel();
		try {		
			Optional<Project> project = helperService.getProject(projectShift.getProjectId());
			projectShift = helperService.getProjectShift(project.get(), projectShift.getHelpDate());
			
			helperService.handupHelper(projectShift);
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}		
		return res;
	}
	
	@RequestMapping(value="/project/shift/request/handdown", method=RequestMethod.POST)
	public ResponseModel handdownHelper(@RequestBody ProjectShift projectShift) {
		ResponseModel res = new ResponseModel();
		try {		
			Optional<Project> project = helperService.getProject(projectShift.getProjectId());
			projectShift = helperService.getProjectShift(project.get(), projectShift.getHelpDate());
			
			helperService.handdownHelper(projectShift);
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}		
		return res;
	}	
	
	@RequestMapping(value="/project/save", method=RequestMethod.POST)
	public ResponseModel createProject(@RequestBody Project project) {
		ResponseModel res = new ResponseModel();
		try {
			helperService.saveProject(project);
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}
		
		return res;
	}
	
	@RequestMapping(value="/project/close", method=RequestMethod.POST)
	public ResponseModel closeProject(@RequestBody Project project) {
		ResponseModel res = new ResponseModel();
		try {
			String[] arrProjectId = project.getId().split(",");
			for(String projectId: arrProjectId) {
				Optional<Project> tempProject = helperService.getProject(projectId);
				if(tempProject.isPresent()) {
					helperService.closeProject(tempProject.get());
				}
			}
			
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}
		
		return res;
	}
	
	@RequestMapping("/project/{projectId}")
	public Project getProject(@PathVariable String projectId){		
		Optional<Project> project = helperService.getProject(projectId);
		return project.get();
	}
	
	@RequestMapping(value="/project/helper/save", method=RequestMethod.POST)
	public ResponseModel saveProjectHelper( @RequestBody Helper helper) {
		ResponseModel res = new ResponseModel();
		
		try {			
			Optional<Project> optProject = helperService.getProject(helper.getProjectId());
		
			if(!optProject.isPresent()) {
				throw new Exception("Project가 존재하지 않습니다. "+helper.getProjectId());
			}
			
			Project project = optProject.get();
			
			Helper helperTemp = helperService.getHelper(helper.getKnoxId());			
			if(helperTemp != null) {
				helperTemp = helperService.updateHelper(helperTemp, helper);
			}else {
				helperTemp = helperService.saveHelper(helper);
			}
			
			List<Helper> helperList = project.getHelpers();
			helperList.add(helperTemp);
			project.setHelpers(helperList);
			
			helperService.saveProject(project);
			
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}
		
		return res;
	}
	
	@RequestMapping(value="/project/helper/delete", method=RequestMethod.POST)
	public ResponseModel deleteProjectHelper( @RequestBody Helper helper) {
		ResponseModel res = new ResponseModel();
		
		try {
			String[] arrKnoxId = helper.getKnoxId().split(",");
			
			Project project = helperService.getProject(helper.getProjectId()).get();
			
			for(String knoxId: arrKnoxId) {
				Helper tempHelper = helperService.getHelper(knoxId);				
				helperService.deleteProjectHelper(project, tempHelper);
			}
			res.setSuccess(true);
		}catch(Exception e) {
			res.setSuccess(false);
			res.setMessage(e.getMessage());
		}
		
		return res;
	}
	
	@RequestMapping("/getProject")
	public Project getProjectList(@RequestParam("id") String id){
		Optional<Project> project = helperService.getProject(id);
		if(project.isPresent()){
			return project.get();
		}		
		return null;
	}
	
	
	@RequestMapping("/getHelper")
	public Helper getHelper(@RequestParam("knoxId") String knoxId){
		Helper helper = helperService.getHelper(knoxId);
		return helper;
	}
}
