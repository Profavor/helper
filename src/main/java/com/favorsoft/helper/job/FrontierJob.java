package com.favorsoft.helper.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.helper.service.impl.HelperServiceImpl;

public class FrontierJob implements Job{
	
	@Autowired
	private HelperService helperService;

	@Override
	@Transactional
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String projectId = (String)context.getMergedJobDataMap().get("projectId");
		
		Project project = helperService.getProject(projectId).get();
		List<ProjectShift> projectShiftList = helperService.getProjectShiftList(projectId, true);
		
		//Request Random
		for(ProjectShift shift: projectShiftList) {
			if(shift.getHelpers().size() < project.getMaxHelperCount()) {
				// Request 요청한 사람들에 대하여 Random 추첨
				List<ShiftHelperRequest> shiftRequestList =  shift.getRequests().stream().filter(s-> s.getStatus().equals(HelperServiceImpl.REQUEST_STATUS_HANDUP)).collect(Collectors.toList());
				
				Random rand = new Random();
				
				while(shiftRequestList.size() > 0) {
					int random = rand.nextInt(shiftRequestList.size());
					ShiftHelperRequest req = shiftRequestList.get(random);
					
					if(existMonthHelper(req.getHelper().getKnoxId())) {
						// 이미 존재하는 아이디는 Request 명단에서 제외 밑 Status 변경
						req.setStatus("MONTH_EXIST");
						helperService.saveShiftHelperRequest(req);				
						shiftRequestList.remove(random);
					}else {
						req.setStatus("SUCCESS");
						helperService.saveShiftHelperRequest(req);
						shift.getHelpers().add(req.getHelper());
					}
					
					if(shift.getHelpers().size() == project.getMaxHelperCount()) {
						shiftRequestList =  shift.getRequests().stream().filter(s-> s.getStatus().equals(HelperServiceImpl.REQUEST_STATUS_HANDUP)).collect(Collectors.toList());
						for(ShiftHelperRequest request: shiftRequestList) {
							request.setStatus("FAIL");
							helperService.saveShiftHelperRequest(request);
						}						
						break;
					}
				}
			}			
		}
		
		//Project Random
		for(ProjectShift shift: projectShiftList) {
			while(shift.getHelpers().size() < project.getMaxHelperCount()) {
				List<Helper> frontierHelper = setHelperCount(shift.getProject());				
				int minHelpCount = frontierHelper.stream().min(Comparator.comparing(Helper::getHelpCount)).get().getHelpCount();
								
				List<Helper> minHelperList = frontierHelper.stream().filter(s->s.getHelpCount() == minHelpCount).collect(Collectors.toList());
				Random rand = new Random();
				int random = rand.nextInt(minHelperList.size());
				
				Helper helper = minHelperList.get(random);
				
				if(!existMonthHelper(helper.getKnoxId())) {	
					shift.getHelpers().add(helper);
				}	
			}
			shift.setStatus(HelperServiceImpl.PROJECT_STATUS_CLOSE);
			helperService.saveProjectShift(shift);
		}		
	}
	
	private List<Helper> setHelperCount(Project project){
		List<Helper> frontierHelper = new ArrayList<Helper>();		
		List<Project> projectList = helperService.getProjectList(false);
		for(Project tempProject: projectList) {
			List<ProjectShift> allShiftList = helperService.getProjectShiftList(tempProject.getId(), false);
			
			List<Helper> helperList = project.getHelpers();		
			for(Helper helper: helperList) {
				  for(ProjectShift shift: allShiftList) {
					  List<Helper> tempHelperList = shift.getHelpers();
					  for(Helper tempHelper: tempHelperList) {
						  if(helper.getKnoxId().equals(tempHelper.getKnoxId())) {
							  helper.setHelpCount(helper.getHelpCount()+1);
						  }
					  }
				  }
				  frontierHelper.add(helper);
			}
		}
		return frontierHelper;
	}
	
	private boolean existMonthHelper(String knoxId) {
		
		List<Project> projectList = helperService.getProjectList(true);
		
		for(Project project: projectList) {
			List<ProjectShift> projectShiftList = helperService.getProjectShiftList(project.getId(), true);
			
			for(ProjectShift shift: projectShiftList) {
				List<Helper> helperList = shift.getHelpers();
				for(Helper tempHelper: helperList) {
					if(knoxId.equals(tempHelper.getKnoxId())) {
						return true;
					}
				}
			}	
		}
		return false;
	}
}
