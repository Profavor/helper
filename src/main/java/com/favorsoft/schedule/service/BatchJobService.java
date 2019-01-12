package com.favorsoft.schedule.service;

import com.favorsoft.schedule.entity.BatchJob;

public interface BatchJobService {
	public BatchJob getBatchJob(String jobName, String jobGroup);
}
