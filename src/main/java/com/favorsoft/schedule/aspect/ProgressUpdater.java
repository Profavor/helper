package com.favorsoft.schedule.aspect;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

public interface ProgressUpdater {
	void update(JobExecutionContext jobExecutionContext) throws SchedulerException;
}
