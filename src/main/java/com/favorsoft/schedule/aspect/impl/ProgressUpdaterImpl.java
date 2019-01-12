package com.favorsoft.schedule.aspect.impl;

import javax.annotation.Resource;

import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.favorsoft.schedule.aspect.ProgressUpdater;
import com.favorsoft.schedule.model.TriggerProgress;

@Component
public class ProgressUpdaterImpl implements ProgressUpdater{
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Resource
	private Scheduler scheduler;

	@Override
	public void update(JobExecutionContext jobExecutionContext) throws SchedulerException {
		TriggerProgress progress = new TriggerProgress();		
		
		Trigger trigger = scheduler.getTrigger(jobExecutionContext.getTrigger().getKey());
		progress.setFinalFireTime(trigger.getFinalFireTime());
		progress.setNextFireTime(trigger.getNextFireTime());
		progress.setPreviousFireTime(trigger.getPreviousFireTime());

		int timesTriggered = 0;
		int repeatCount = 0;

		if (trigger instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
			timesTriggered = simpleTrigger.getTimesTriggered();
			repeatCount = simpleTrigger.getRepeatCount();
		} else if (trigger instanceof DailyTimeIntervalTrigger) {
			DailyTimeIntervalTrigger dailyTrigger = (DailyTimeIntervalTrigger) trigger;
			timesTriggered = dailyTrigger.getTimesTriggered();
			repeatCount = dailyTrigger.getRepeatCount();
		}

		Trigger jobTrigger = jobExecutionContext.getTrigger();
		if (jobTrigger != null && jobTrigger.getJobKey() != null) {
			progress.setJobKey(jobTrigger.getJobKey().getName());
			progress.setJobClass(jobTrigger.getClass().getSimpleName());
			progress.setTimesTriggered(timesTriggered);
			progress.setRepeatCount(repeatCount + 1);
		}

		messagingTemplate.convertAndSend("/topic/progress", progress);		
	}

}
