package com.favorsoft.schedule.service;

import org.quartz.SchedulerException;

import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.entity.BatchJobTrigger;
import com.favorsoft.schedule.model.ScheduleResponse;

public interface QuartzService {

	public ScheduleResponse register(BatchJob batchJob) throws SchedulerException, ClassNotFoundException;

    public ScheduleResponse delete(BatchJob batchJob) throws SchedulerException;

    public ScheduleResponse pause(BatchJob batchJob) throws SchedulerException;

    public ScheduleResponse resume(BatchJob batchJob) throws SchedulerException;

    public boolean checkExistSchedule(BatchJob batchJob) throws SchedulerException;

    public ScheduleResponse immediatelyExecution(BatchJob batchJob) throws SchedulerException, ClassNotFoundException;

	void unScheduleTrigger(BatchJobTrigger trigger) throws SchedulerException;

	void reScheduleTrigger(BatchJobTrigger trigger, String triggerValue) throws SchedulerException;
	
	void addScheduleTrigger(BatchJobTrigger batchJobTrigger) throws SchedulerException;
}
