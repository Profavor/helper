package com.favorsoft.schedule.job;

import org.quartz.JobExecutionContext;

import com.favorsoft.schedule.model.LogRecord;
import com.favorsoft.schedule.model.LogRecord.LogType;

public class BatchJobExecutor extends AbstractLoggingJob {

	@Override
	public LogRecord executionTask(JobExecutionContext context) {
		
		System.out.println(context.getJobDetail().getKey().getGroup()+"."+context.getJobDetail().getKey().getName());
		System.out.println("test12345");
		return new LogRecord(LogType.INFO, "Hello!");
	}

}
