package com.favorsoft.schedule.controller;

import javax.annotation.Resource;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.entity.BatchJobTrigger;
import com.favorsoft.schedule.model.ScheduleResponse;
import com.favorsoft.schedule.repository.BatchJobRepository;
import com.favorsoft.schedule.repository.BatchJobTriggerRepository;
import com.favorsoft.schedule.service.QuartzService;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {
	private final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

	@Resource
	private Scheduler scheduler;
	
	@Autowired
	private BatchJobRepository batchJobRepository;
	
	@Autowired
	private BatchJobTriggerRepository batchJobTriggerRepository;
	
	@Autowired
	private QuartzService quartzService;

	@RequestMapping(value= "/regist", method=RequestMethod.POST)
	public ScheduleResponse registJob(@RequestBody BatchJob batchJob) {
		ScheduleResponse response = null;		
		try {
			if(!scheduler.checkExists(new JobKey("JOBNAME", "JOBGROUP"))){
				quartzService.register(batchJob);
				response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job regist success!!");
			}else {
				response = new ScheduleResponse(false, "Job이 이미 존재합니다.");
			}
						
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
			logger.error(e.getLocalizedMessage(), e);
		}		
		
		return response;
	}
	
	@RequestMapping("/{jobGroup}/{jobName}/deleteJob")
	public ScheduleResponse deleteJob(@PathVariable String jobGroup, @PathVariable String jobName) {
		ScheduleResponse response = null;
		BatchJob batchJob = new BatchJob(jobName, jobGroup);		
		try {
			quartzService.delete(batchJob);
			response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job delete success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping("/{jobGroup}/{jobName}/pause")
	public ScheduleResponse pauseJob(@PathVariable String jobGroup, @PathVariable String jobName) {
		ScheduleResponse response = null;
		BatchJob batchJob = batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);		
		try {
			quartzService.pause(batchJob);
			response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job pause success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping("/{jobGroup}/{jobName}/resume")
	public ScheduleResponse resumeJob(@PathVariable String jobGroup, @PathVariable String jobName) {
		ScheduleResponse response = null;
		BatchJob batchJob = batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);		
		try {
			quartzService.resume(batchJob);
			response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job resume success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping("/{jobGroup}/{jobName}/execution")
	public ScheduleResponse immediatelyExecution(@PathVariable String jobGroup, @PathVariable String jobName) {
		ScheduleResponse response = null;
		BatchJob batchJob = batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);		
		try {
			quartzService.immediatelyExecution(batchJob);
			response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job immediatelyExecution success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping("/{jobGroup}/{jobName}/addScheduleTrigger")
	public ScheduleResponse addScheduleTrigger(@PathVariable String jobGroup, @PathVariable String jobName, @RequestBody BatchJobTrigger batchJobTrigger) {
		ScheduleResponse response = null;
		BatchJob batchJob = batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);
		batchJobTrigger.setBatchJob(batchJob);
		try {
			quartzService.addScheduleTrigger(batchJobTrigger);
			response = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job addScheduleTrigger success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping(value="/{triggerGroup}/{triggerName}/reScheduleTrigger", method=RequestMethod.POST)
	public ScheduleResponse reSchedule(@PathVariable String triggerGroup, @PathVariable String triggerName, @RequestParam(name="triggerValue") String triggerValue) {
		ScheduleResponse response = null;
		BatchJobTrigger trigger = batchJobTriggerRepository.findByTriggerNameAndTriggerGroup(triggerName, triggerGroup);
		try {
			quartzService.reScheduleTrigger(trigger, triggerValue);
			response = new ScheduleResponse(true, "Trigger reScheduleTrigger success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
	
	@RequestMapping("/{triggerGroup}/{triggerName}/unScheduleTrigger")
	public ScheduleResponse unSchedule(@PathVariable String triggerGroup, @PathVariable String triggerName) {
		ScheduleResponse response = null;
		BatchJobTrigger trigger = batchJobTriggerRepository.findByTriggerNameAndTriggerGroup(triggerName, triggerGroup);
		try {
			quartzService.unScheduleTrigger(trigger);
			response = new ScheduleResponse(true, "Trigger unScheduleTrigger success!!");			
		}catch(Exception e) {
			response = new ScheduleResponse(false, e.getLocalizedMessage());
		}	
		
		return response;
	}
}
