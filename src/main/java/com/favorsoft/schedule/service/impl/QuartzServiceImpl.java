package com.favorsoft.schedule.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.entity.BatchJobParams;
import com.favorsoft.schedule.entity.BatchJobTrigger;
import com.favorsoft.schedule.model.ScheduleResponse;
import com.favorsoft.schedule.repository.BatchJobRepository;
import com.favorsoft.schedule.repository.BatchJobTriggerRepository;
import com.favorsoft.schedule.service.QuartzService;

@Service
@Transactional(readOnly = true)
public class QuartzServiceImpl implements QuartzService {

	private static final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);

	private static final String KEY_QUEUE_TYPE = "queueType";
	private static final String KEY_JOB = "job";

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private BatchJobRepository batchJobRepository;
	
	@Autowired
	private BatchJobTriggerRepository batchJobTriggerRepository;

	@PreDestroy
	public void shutdownQuartzSchedule() {
		try {
			logger.debug("Shutdown Quartz Scheduler.");
			schedulerFactoryBean.getScheduler().shutdown();
		} catch (SchedulerException e) {
			logger.error("Occur SchedulerException: {}", e);
		}
	}

	@Override
	@Transactional
	public ScheduleResponse register(BatchJob batchJob) throws SchedulerException, ClassNotFoundException {
		ScheduleResponse scheduleResponse = null;
		
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobDetail jobDetail = concreteJobDetail(batchJob);
		List<Trigger> triggers = concreteCronTrigger(batchJob);
		
		for(int i=0; i<triggers.size(); i++) {
			Trigger trigger = triggers.get(i);
			if(i==0) {
				scheduler.scheduleJob(jobDetail, trigger);
			}else {
				scheduler.scheduleJob(trigger);
			}
		}

		scheduleResponse = new ScheduleResponse(true, jobDetail.getKey().getName(),	jobDetail.getKey().getGroup(), "Job regist Successfully!");

		batchJobRepository.save(batchJob);

		return scheduleResponse;
	}

	@SuppressWarnings("unchecked")
	private JobDetail concreteJobDetail(BatchJob batchJob) throws ClassNotFoundException {
		Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(batchJob.getClassName());
		JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(batchJob.getJobName(), batchJob.getJobGroup()).build();
		jobDetail.getJobDataMap().put(KEY_JOB, jobDetail.getKey());
		jobDetail.getJobDataMap().put(KEY_QUEUE_TYPE, batchJob.getQueueType());
		
		for(BatchJobParams param : batchJob.getBatchJobParams()) {
			jobDetail.getJobDataMap().put(param.getParamKey(), param.getParamValue());
		}
		
		return jobDetail;
	}

	private List<Trigger> concreteCronTrigger(BatchJob batchJob) {
		List<Trigger> triggerList = new ArrayList<Trigger>();
		for(int i=0; i<batchJob.getBatchJobTriggers().size(); i++) {
			BatchJobTrigger trigger = batchJob.getBatchJobTriggers().get(i);
			Trigger t = null;
			if(i==0) {
				t = TriggerBuilder.newTrigger().withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup())
						.startNow().withSchedule(CronScheduleBuilder.cronSchedule(trigger.getTriggerValue()).inTimeZone(TimeZone.getTimeZone("Asia/Seoul"))).build();
			}else {
				t = TriggerBuilder.newTrigger().withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup()).forJob(batchJob.getJobName(), batchJob.getJobGroup())
						.startNow().withSchedule(CronScheduleBuilder.cronSchedule(trigger.getTriggerValue()).inTimeZone(TimeZone.getTimeZone("Asia/Seoul"))).build();
			}
			triggerList.add(t);
		}

		return triggerList;
	}

	@Override
	@Transactional
	public ScheduleResponse delete(BatchJob batchJob) throws SchedulerException {
		
		BatchJob temp = batchJobRepository.findByJobNameAndJobGroup(batchJob.getJobName(), batchJob.getJobGroup());
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = generateJobKey(batchJob);
		scheduler.deleteJob(jobKey);
		
		if(temp != null) {
			batchJobRepository.delete(temp);
		}
		
		return new ScheduleResponse(true, jobKey.getName()+" has deleted.");
	}

	@Override
	@Transactional
	public ScheduleResponse pause(BatchJob batchJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = generateJobKey(batchJob);
		scheduler.pauseJob(jobKey);
		for (BatchJobTrigger trigger : batchJob.getBatchJobTriggers()) {
			TriggerKey triggerKey = new TriggerKey(trigger.getTriggerName(), trigger.getTriggerGroup());
			scheduler.pauseTrigger(triggerKey);
		}
		batchJob.setStatus("PAUSE");
		batchJobRepository.save(batchJob);
        
		return new ScheduleResponse(true, jobKey.getName()+" has paused.");
	}

	@Override
	@Transactional
	public ScheduleResponse resume(BatchJob batchJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = generateJobKey(batchJob);
		scheduler.resumeJob(jobKey);
		for (BatchJobTrigger trigger : batchJob.getBatchJobTriggers()) {
			TriggerKey triggerKey = new TriggerKey(trigger.getTriggerName(), trigger.getTriggerGroup());
			scheduler.resumeTrigger(triggerKey);
		}		
		batchJob.setStatus("RUN");
		batchJobRepository.save(batchJob);
		
		return  new ScheduleResponse(true, jobKey.getName()+" has resumed.");
	}

	@Override
	public boolean checkExistSchedule(BatchJob batchJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = generateJobKey(batchJob);
		return scheduler.checkExists(jobKey);
	}

	@Override
	@Transactional
	public ScheduleResponse immediatelyExecution(BatchJob batchJob) throws SchedulerException, ClassNotFoundException {
		ScheduleResponse scheduleResponse = null;
		
		Scheduler scheduler = schedulerFactoryBean.getScheduler();		
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(UUID.randomUUID().toString(), "IMMEDIATELY").forJob(batchJob.getJobName(), batchJob.getJobGroup()).startNow().build();
		
		scheduler.scheduleJob(trigger);		
		scheduleResponse = new ScheduleResponse(true, batchJob.getJobName(), batchJob.getJobGroup(), "Job immediately Execution");
		
		return scheduleResponse;
	}

	
	
	private JobKey generateJobKey(BatchJob batchJob) {
		return new JobKey(batchJob.getJobName(), batchJob.getJobGroup());
	}
	
	private TriggerKey generateTriggerKey(BatchJobTrigger batchJobTrigger) {
		return new TriggerKey(batchJobTrigger.getTriggerName(), batchJobTrigger.getTriggerGroup());
	}

	@Override
	public void unScheduleTrigger(BatchJobTrigger trigger) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();		
		scheduler.unscheduleJob(generateTriggerKey(trigger));		
		batchJobTriggerRepository.delete(trigger);
	}

	@Override
	public void reScheduleTrigger(BatchJobTrigger trigger, String triggerValue) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		
		TriggerKey triggerKey = generateTriggerKey(trigger);
		
		Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup())
				.startNow().withSchedule(CronScheduleBuilder.cronSchedule(triggerValue).inTimeZone(TimeZone.getTimeZone("Asia/Seoul"))).build();
		scheduler.rescheduleJob(triggerKey, newTrigger);
		
		
		trigger.setTriggerValue(triggerValue);		
		batchJobTriggerRepository.save(trigger);
	}

	@Override
	public void addScheduleTrigger(BatchJobTrigger trigger) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		
		JobDetail jobDetail = scheduler.getJobDetail(new JobKey(trigger.getBatchJob().getJobName(), trigger.getBatchJob().getJobGroup()));
		
		Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup()).forJob(jobDetail)
				.startNow().withSchedule(CronScheduleBuilder.cronSchedule(trigger.getTriggerValue()).inTimeZone(TimeZone.getTimeZone("Asia/Seoul"))).build();

		scheduler.scheduleJob(newTrigger);		
				
		batchJobTriggerRepository.save(trigger);
	}

}
