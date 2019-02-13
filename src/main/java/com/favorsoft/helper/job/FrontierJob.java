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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.helper.service.impl.HelperServiceImpl;

public class FrontierJob implements Job {

	public final static String PROJECT_STATUS_OPEN = "OPEN";
	public final static String PROJECT_STATUS_CLOSE = "CLOSE";	
	public final static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	
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

				while (!shiftRequestList.isEmpty()) {
					int random = rand.nextInt(shiftRequestList.size());
					ShiftHelperRequest req = shiftRequestList.get(random);

					if (existMonthHelper(req.getHelper().getKnoxId(), addHelperList, shift.getHelpDate())) {
						// 이미 존재하는 아이디는 Request 명단에서 제외 밑 Status 변경
						req.setStatus("MONTH_EXIST");
						//helperService.saveShiftHelperRequest(req);
						shiftRequestList.remove(random);
					} else if(shift.getHelpers().size() < project.getMaxHelperCount()) {
						req.setStatus("SUCCESS");
						//helperService.saveShiftHelperRequest(req);

						shift.getHelpers().add(req.getHelper());
						addHelperList.add(req.getHelper());
						shiftRequestList.remove(random);						
					}

					if (shift.getHelpers().size() >= project.getMaxHelperCount()) {
						break;
					}
				}

				for (ShiftHelperRequest reqs : shiftRequestList) {
					reqs.setStatus("FAIL");
					//helperService.saveShiftHelperRequest(reqs);
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

				if (!existMonthHelper(helper.getKnoxId(), addHelperList, shift.getHelpDate())) {
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
		
		// 메일 보내기
		// 1. 선정됨 봉사자에게 메일
		for (ProjectShift shift : projectShiftList) {
			List<Helper> helperList = shift.getHelpers();
			for (Helper helper : helperList) {				
				MimeMessage message = javaMailSender.createMimeMessage();				
		        try {
		            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
		            messageHelper.setSubject("[HELPER] 봉사 안내 메일");
		            messageHelper.setTo(helper.getKnoxId() + "@miracom.co.kr");
		            messageHelper.setCc("ehli@nate.com");
		            messageHelper.setFrom("labs.prusoft@gmail.com");
		            messageHelper.setText(makeMail(shift.getProject().getProjectName(), yyyyMMdd.format(shift.getHelpDate()), shift.getProject().getDescription(), shift.getProject().getEducationUrl(), helperList), true);
		            javaMailSender.send(message);
		            
		        } catch (MessagingException e) {
		            e.printStackTrace();
		        }				
			}
		}	
		
		// 2. 담당자에게 선정된 시트와 봉사자 확인 메일
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setSubject("[HELPER] 봉사자 선정 메일");
			messageHelper.setTo("profavor.lin@miracom.co.kr");
			messageHelper.setCc("ehli@nate.com");
			messageHelper.setFrom("labs.prusoft@gmail.com");
			messageHelper.setText(makeMail4admin(project, projectShiftList), true);
			javaMailSender.send(message);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public String makeMail4admin(Project project, List<ProjectShift> projectShiftList) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<meta charset='utf-8' />");
		sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1' />");
		sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0'>");
		sb.append("<table align='center' border='0' cellpadding='0' cellspacing='0' width='500' style='color: #5a5252;'>");
		sb.append("	<tr>");
		sb.append("		<td style='font-size: 0; line-height: 0;' height='10'>&nbsp;</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td align='center'>");
		sb.append("			<h1 style='font-family: Impact, Charcoal, sans-serif; font-weight: bold; font-size: 80px; margin: 0px 0px 0px 0px'>");
		sb.append("				HELPER.");
		sb.append("			</h1>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td align='center'>");
		sb.append("			<h2 style='margin: 0px 0px 0px 0px'>봉사자 선정 메일</h2>");
		sb.append("			<div>");
		sb.append("				아래와 같이 봉사자가 선정 되었습니다.");
		sb.append("			</div>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td bgcolor='#ffffff' style='padding: 10px 30px 20px 30px;'>");
		sb.append("			<table border='0' cellpadding='0' cellspacing='0' width='100%'>");
		sb.append("				<tr>");
		sb.append("					<td>");
		sb.append("						<h3 style='margin-bottom: 10px'>봉사</h3>");
		sb.append("						<div>"+ project.getProjectName() +"</div>");
		sb.append("						<div style='font-size: 14px; margin: 4px'>"+ project.getDescription() +"</div>");
		sb.append("						<h3 style='margin-bottom: 10px'>일자별 봉사자</h3>");
		
		for (ProjectShift shift : projectShiftList) {
			
			List<Helper> helperList = shift.getHelpers();
			if(helperList.size() == 0) continue;
			
			sb.append("						<div style='margin: 5px 5px 15px 5px'>");
			sb.append("							<div style='margin: 6px'>"+yyyyMMdd.format(shift.getHelpDate())+"</div>");
			
			for (Helper helper : helperList) {	
				sb.append("							<div style='font-size: 14px; margin: 5px 5px 5px 10px'>"+helper.getUserName()+" "+helper.getKnoxId()+"@miracom.co.kr</div>");
			}
			sb.append("						</div>");
		}
	
		sb.append("					</td>");
		sb.append("				</tr>");
		sb.append("			</table>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td bgcolor='#ee4c50' style='text-align: center; padding: 10px 0 10px 0;'>");
		sb.append("			<a style='font-weight: bold; color: #ffffff; font-size: 15px; text-decoration: none;' href='http://labs.prusoft.space' target='_blank'>");
		sb.append("				http://labs.prusoft.space");
		sb.append("			</a>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		
		return sb.toString();
	}


	public String makeMail(String projectName, String helpDate, String description, String url, List<Helper> helperList) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<meta charset='utf-8' />");
		sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1' />");
		sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0'>");
		sb.append("<table align='center' border='0' cellpadding='0' cellspacing='0' width='500' style='color: #5a5252'>");
		sb.append("	<tr>");
		sb.append("		<td style='font-size: 0; line-height: 0;' height='10'>&nbsp;</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td align='center'>");
		sb.append("			<h1 style='font-family: Impact, Charcoal, sans-serif; font-weight: bold; font-size: 80px; margin:0px 0px 0px 0px'>HELPER.</h1>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td align='center'>");
		sb.append("			<h2 style='margin: 0px 0px 0px 0px'>봉사 안내 메일</h2>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td bgcolor='#ffffff' style='padding: 10px 30px 40px 30px;'>");
		sb.append("			<table border='0' cellpadding='0' cellspacing='0' width='100%'>");
		sb.append("				<tr>");
		sb.append("					<td>");
		sb.append("						<h3 style='margin-bottom: 10px'>봉사</h3>");
		sb.append("						<div>" + projectName + "</div>");
								
		sb.append("						<h3 style='margin-bottom: 10px'>일자</h3>");
		sb.append("						<div>"+ helpDate +"</div>");
								
		sb.append("						<h3 style='margin-bottom: 10px'>봉사자</h3>");
		
		for (Helper helper : helperList) {
			sb.append("						<div>"+helper.getUserName()+" "+helper.getKnoxId()+"@miracom.co.kr</div>");
		}
								
		sb.append("						<h3 style='margin-bottom: 10px'>내용 </h3>");
		sb.append("						<div>"+ description +"</div>");
								
		sb.append("						<h3 style='margin-bottom: 10px'>참고 URL</h3>");
		sb.append("						<div>");
		sb.append("							<a style='text-decoration:none;' href='"+ url +"' target='_blank'>"+ url +"</a>");
		sb.append("						</div>");
		sb.append("					</td>");
		sb.append("				</tr>");
		
		sb.append("				<tr>");
		sb.append("					<td style='padding: 30px 0px 0px 0px;'>");
		sb.append("						<h4 style='margin-bottom: 5px;'>※ 봉사자 안내 사항</h4>");
		sb.append("						1. 봉사 전 외근 신청은 필수!");
		sb.append("						<br>2. 봉사후기를 커뮤니티에 올려주세요.");
		sb.append("					</td>");
		sb.append("				</tr>");
		
		sb.append("			</table>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("	<tr>");
		sb.append("		<td bgcolor='#ee4c50' style='text-align: center; padding: 10px 0 10px 0;'>");
		sb.append("			<a style='font-weight:bold; color: #ffffff; font-size: 15px; text-decoration:none; ' href='http://labs.prusoft.space' target='_blank'>http://labs.prusoft.space</a>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		
		return sb.toString();
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
			if(helper.isEnable()) {
				frontierHelper.add(helper);
			}			
		}
		return frontierHelper;
	}

	// 봉사는 한달에 한번 갈 수 있다. 즉 한달 기간동안의 봉사자만 추리도록 변경
	private boolean existMonthHelper(String knoxId, List<Helper> addHelperList, Date helpDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.setTime(helpDate);

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
