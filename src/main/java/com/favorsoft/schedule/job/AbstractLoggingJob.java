package com.favorsoft.schedule.job;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.favorsoft.schedule.aspect.ProgressUpdater;
import com.favorsoft.schedule.model.LogRecord;

public abstract class AbstractLoggingJob extends QuartzJobBean {
	private static final Logger log = LoggerFactory.getLogger(AbstractLoggingJob.class);

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Resource
	private ProgressUpdater progressUpdater;

	/**
	 *
	 * @param jobExecutionContext
	 * @return final log
	 */
	public abstract LogRecord executionTask(JobExecutionContext jobExecutionContext);

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LogRecord logMsg = executionTask(jobExecutionContext);
		logAndSend(logMsg);
		try {
			progressUpdater.update(jobExecutionContext);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void logAndSend(LogRecord logRecord) {
		log.info(logRecord.getMessage());
		logRecord.setThreadName(Thread.currentThread().getName());
		messagingTemplate.convertAndSend("/topic/logs", logRecord);
	}
}
