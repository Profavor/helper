package com.favorsoft.schedule.service;

import java.util.Date;
import java.util.Optional;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.entity.BatchJobHistory;
import com.favorsoft.schedule.repository.BatchJobHistoryRepository;
import com.favorsoft.schedule.repository.BatchJobRepository;
import com.google.gson.Gson;

@Component
public class JobListenerService implements JobListener {
	@Autowired
	private BatchJobRepository batchJobRepository;
	
	@Autowired
	private BatchJobHistoryRepository batchJobHistoryRepository;
	
	private static Logger logger = LoggerFactory.getLogger(JobListenerService.class);
	
	private String name = "SchedulerGlobalListener";
	
	public void setGlobalListenerName(String name) {
        this.name = name;
    }

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {	

		Gson gson = new Gson();
		String jobName = context.getJobDetail().getKey().getName();
		String jobGroup = context.getJobDetail().getKey().getGroup();
		BatchJob batchJob = batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);
		if(batchJob != null) {
			BatchJobHistory history = new BatchJobHistory();
			history.setId(context.getFireInstanceId());
			history.setStatus("EXECUTING");
			history.setStartExecuteDate(new Date());
			history.setClassName(batchJob.getClassName());
			history.setJobParams(gson.toJson(context.getJobDetail().getJobDataMap()));
			batchJobHistoryRepository.save(history);		
		}		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info(context.getJobDetail().getKey().getGroup()+"."+context.getJobDetail().getKey().getName()+" is started.");
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {		
		Optional<BatchJobHistory> history = batchJobHistoryRepository.findById(context.getFireInstanceId());
		
		if(history.isPresent()) {
			BatchJobHistory batchJobHistory = history.get();
			batchJobHistory.setStatus("FINISH");
			batchJobHistory.setEndExecuteDate(new Date());
			if(jobException != null) {
				batchJobHistory.setStatus("ERROR");
				batchJobHistory.setMessage(jobException.getLocalizedMessage());
			}
			batchJobHistoryRepository.save(batchJobHistory);
		}
	}

}
