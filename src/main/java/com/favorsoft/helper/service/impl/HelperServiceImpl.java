package com.favorsoft.helper.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;
import com.favorsoft.helper.repository.HelperRepository;
import com.favorsoft.helper.repository.ProjectRepository;
import com.favorsoft.helper.repository.ProjectShiftRepository;
import com.favorsoft.helper.repository.ShiftHelperRequestRepository;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.entity.BatchJobParams;
import com.favorsoft.schedule.entity.BatchJobTrigger;
import com.favorsoft.schedule.service.BatchJobService;
import com.favorsoft.schedule.service.QuartzService;

@Transactional
@Service
public class HelperServiceImpl implements HelperService{
	public final static String PROJECT_STATUS_OPEN = "OPEN";
	public final static String PROJECT_STATUS_CLOSE = "CLOSE";
	public final static String REQUEST_STATUS_HANDUP = "HAND_UP";
	public final static String REQUEST_STATUS_HANDDOWN = "HAND_DOWN";
	
	@Autowired
	private HelperRepository helperRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectShiftRepository projectShiftRepository;
	
	@Autowired
	private ShiftHelperRequestRepository shiftHelperRequestRepositry;
	
	@Autowired
	private QuartzService quartzService;
	
	@Autowired
	private BatchJobService batchJobService;

	@Override
	public Helper getHelper(String knoxId) {
		return helperRepository.findByKnoxId(knoxId);
	}

	@Override
	public void saveProject(Project project) throws ClassNotFoundException, SchedulerException {
		if(!CronExpression.isValidExpression(project.getTriggerValue())) {
			throw new SchedulerException("Cron expression is not valid.");
		}
		
		Optional<Project> tempProject = projectRepository.findById(project.getId());
		
		if(!tempProject.isPresent()) {
			project = projectRepository.saveAndFlush(project);
			
			BatchJob batchJob = new BatchJob();
			batchJob.setJobName(project.getId());
			batchJob.setJobGroup("HELPER");
			batchJob.setDescription(project.getDescription());
			batchJob.setQueueType("CRON");
			batchJob.setClassName("com.favorsoft.helper.job.FrontierJob");
			
			List<BatchJobParams> batchJobParams = new ArrayList<BatchJobParams>();
			BatchJobParams batchJobParam = new BatchJobParams("projectId", project.getId());
			batchJobParam.setBatchJob(batchJob);
			batchJobParams.add(batchJobParam);	
			batchJob.setBatchJobParams(batchJobParams);
			
			List<BatchJobTrigger> batchJobTriggers = new ArrayList<BatchJobTrigger>();
			BatchJobTrigger batchJobTrigger = new BatchJobTrigger(batchJob.getJobName(), batchJob.getJobGroup(), project.getTriggerValue());
			batchJobTrigger.setBatchJob(batchJob);
			batchJobTriggers.add(batchJobTrigger);
			batchJob.setBatchJobTriggers(batchJobTriggers);	
			
			quartzService.register(batchJob);
		}else {
			if(!tempProject.get().getTriggerValue().equals(project.getTriggerValue())) {
				BatchJob batchJob = batchJobService.getBatchJob(project.getId(), "HELPER");
				List<BatchJobTrigger> batchJobTriggers = batchJob.getBatchJobTriggers();
				batchJobTriggers.get(0).setTriggerValue(project.getTriggerValue());
				
				quartzService.register(batchJob);
			}
		}		
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
		if(projectShift.getProjectId() != null) {
			Optional<Project> project = getProject(projectShift.getProjectId());
			if(project.isPresent() && projectShift.getProject() == null) {
				projectShift.setProject(project.get());
			}		
		}
		
		projectShiftRepository.save(projectShift);		
	}
	
	@Override
	public void handupHelper(ProjectShift projectShift) throws Exception {
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		
		Helper helper = getHelper(auth.getName());
		ShiftHelperRequest shiftHelperRequest = shiftHelperRequestRepositry.findByProjectShiftAndHelper(projectShift, helper);
		if(shiftHelperRequest == null) {
			shiftHelperRequest = new ShiftHelperRequest(projectShift, helper);
		}		
		
		shiftHelperRequest.setRequestDate(new Date());
		shiftHelperRequest.setStatus(REQUEST_STATUS_HANDUP);
		
		boolean exist = false;
		for(ShiftHelperRequest request: projectShift.getRequests()) {
			if(auth.getName().equals(request.getHelper().getKnoxId())){
				request.setRequestDate(new Date());
				request.setStatus(REQUEST_STATUS_HANDUP);
				exist = true;
				break;
			}			
		}
		
		if(!exist) {
			projectShift.getRequests().add(shiftHelperRequest);
		}
		
		projectShiftRepository.save(projectShift);	
	}
	
	@Override
	public void handdownHelper(ProjectShift projectShift) throws Exception {
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		
		Helper helper = getHelper(auth.getName());
		ShiftHelperRequest shiftHelperRequest = shiftHelperRequestRepositry.findByProjectShiftAndHelper(projectShift, helper);
		if(shiftHelperRequest == null) {
			throw new Exception("지원 이력이 존재하지 않는 봉사자입니다.");
		}		
		shiftHelperRequest.setRequestDate(new Date());
		shiftHelperRequest.setStatus(REQUEST_STATUS_HANDDOWN);
		
		shiftHelperRequestRepositry.save(shiftHelperRequest);
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
	public List<ProjectShift> getProjectShiftList(String projectId, String projectStatus) {
		Project project = getProject(projectId).get();
		
		if(StringUtils.isEmpty(projectStatus)) {
			return projectShiftRepository.findByProject(project);
		}else {
			return projectShiftRepository.findByProjectAndStatus(project, projectStatus);
		}		
	}

	@Override
	public void updateProjectShift(ProjectShift oldProjectShift, ProjectShift newProjectShift) {
		oldProjectShift.setStatus(newProjectShift.getStatus());		
		oldProjectShift.setHelpers(newProjectShift.getHelpers());
		projectShiftRepository.save(oldProjectShift);
	}

	@Override
	public ProjectShift getProjectShift(Project project, Date helpDate) {
		return  projectShiftRepository.findByProjectAndHelpDate(project, helpDate);
	}

	@Override
	public List<ProjectShift> getProjectShiftBetweenHelpDate(Project project, Date startDate, Date endDate) {
		return projectShiftRepository.findByProjectAndHelpDateBetween(project, startDate, endDate);
	}
}
