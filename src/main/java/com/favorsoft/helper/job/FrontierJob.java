package com.favorsoft.helper.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.helper.service.impl.HelperServiceImpl;

public class FrontierJob implements Job {

	public final static String PROJECT_STATUS_OPEN = "OPEN";
	public final static String PROJECT_STATUS_CLOSE = "CLOSE";	
	
	@Autowired
	private HelperService helperService;

	@Autowired
    public JavaMailSender javaMailSender;
	
	@Override
	@Transactional
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String projectId = (String) context.getMergedJobDataMap().get("projectId");

		Project project = helperService.getProject(projectId).get();
		List<ProjectShift> projectShiftList = helperService.getProjectShiftList(projectId, PROJECT_STATUS_OPEN);
		List<Helper> addHelperList = new ArrayList<Helper>();
		// Request Random
		for (ProjectShift shift : projectShiftList) {
			if (shift.getHelpers().size() < project.getMaxHelperCount()) {
				// Request 요청한 사람들에 대하여 Random 추첨
				List<ShiftHelperRequest> shiftRequestList = shift.getRequests().stream()
						.filter(s -> s.getStatus().equals(HelperServiceImpl.REQUEST_STATUS_HANDUP))
						.collect(Collectors.toList());

				Random rand = new Random();

				for (int i = 0; i < shiftRequestList.size(); i++) {
					int random = rand.nextInt(shiftRequestList.size());
					ShiftHelperRequest req = shiftRequestList.get(random);

					if (existMonthHelper(req.getHelper().getKnoxId(), addHelperList)) {
						// 이미 존재하는 아이디는 Request 명단에서 제외 밑 Status 변경
						req.setStatus("MONTH_EXIST");
						helperService.saveShiftHelperRequest(req);
						shiftRequestList.remove(random);
					} else {
						req.setStatus("SUCCESS");
						helperService.saveShiftHelperRequest(req);

						shift.getHelpers().add(req.getHelper());
						addHelperList.add(req.getHelper());
						shiftRequestList.remove(random);
					
						String to = req.getHelper().getKnoxId() + "@miracom.co.kr";
						String subject = "[TEST][Helper] 봉사 안내 메일";
						String text = "봉사가세요!";
						
						sendSimpleMessage(to, subject, text);
						
					}

					if (shift.getHelpers().size() == project.getMaxHelperCount()) {
						break;
					}
				}

				for (ShiftHelperRequest reqs : shiftRequestList) {
					reqs.setStatus("FAIL");
					helperService.saveShiftHelperRequest(reqs);
				}
			}
		}

		// Project Random
		for (ProjectShift shift : projectShiftList) {
			List<Helper> frontierHelper = setHelperCount(shift.getProject());
			int count = shift.getHelpers().size();
			while (count < project.getMaxHelperCount() && !frontierHelper.isEmpty()) {
				int minHelpCount = frontierHelper.stream().min(Comparator.comparing(Helper::getHelpCount)).get()
						.getHelpCount();

				List<Helper> minHelperList = frontierHelper.stream().filter(s -> s.getHelpCount() == minHelpCount)
						.collect(Collectors.toList());
				Random rand = new Random();
				int random = rand.nextInt(minHelperList.size());

				Helper helper = minHelperList.get(random);

				if (!existMonthHelper(helper.getKnoxId(), addHelperList)) {
					shift.getHelpers().add(helper);
					count++;
					addHelperList.add(helper);
				} else {
					frontierHelper.remove(helper);
				}							
			}
			if (count == project.getMaxHelperCount()) {
				shift.setStatus(HelperServiceImpl.PROJECT_STATUS_CLOSE);
				helperService.saveProjectShift(shift);
			}
		}
		
		
		String to = "ehli@nate.com";
		String subject = "[TEST][Helper] 봉사 안내 메일";
		String text = "봉사가세요!";
		
		sendSimpleMessage(to, subject, text);
		
	}

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

	private List<Helper> setHelperCount(Project project) {
		List<Helper> helperList = project.getHelpers();

		List<Helper> frontierHelper = new ArrayList<Helper>();

		List<Project> allProject = helperService.getProjectList(false);

		for (Helper helper : helperList) {
			for (Project projects : allProject) {
				List<ProjectShift> allShiftList = helperService.getProjectShiftList(projects.getId(), null);
				for (ProjectShift shift : allShiftList) {
					List<Helper> tempHelperList = shift.getHelpers();
					for (Helper tempHelper : tempHelperList) {
						if (helper.getKnoxId().equals(tempHelper.getKnoxId())) {
							helper.setHelpCount(helper.getHelpCount() + 1);
						}
					}
				}
			}
			frontierHelper.add(helper);
		}
		return frontierHelper;
	}

	// 봉사는 한달에 한번 갈 수 있다. 즉 한달 기간동안의 봉사자만 추리도록 변경
	private boolean existMonthHelper(String knoxId, List<Helper> addHelperList) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, +1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = cal.getTime();
		String strStartDate = formatter.format(startDate);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = cal.getTime();
		String strEndDate = formatter.format(endDate);
		try {
			startDate = formatter.parse(strStartDate.substring(0, 10) + " 00:00:00");
			endDate = formatter.parse(strEndDate.substring(0, 10) + " 23:59:59");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 전체 프로젝트 리스트를 불러온다 (종료된 프로젝트 포함)
		List<Project> projectList = helperService.getProjectList(false);

		for (Project project : projectList) {
			
			List<ProjectShift> projectShiftList = helperService.getProjectShiftBetweenHelpDate(project, startDate, endDate);

			for (ProjectShift shift : projectShiftList) {
				List<Helper> helperList = shift.getHelpers();
				for (Helper tempHelper : helperList) {
					if (knoxId.equals(tempHelper.getKnoxId())) {
						return true;
					}
				}
			}
		}

		for (Helper helper : addHelperList) {
			if (knoxId.equals(helper.getKnoxId())) {
				return true;
			}
		}

		return false;
	}
}
