package com.favorsoft.schedule.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.favorsoft.schedule.entity.BatchJob;
import com.favorsoft.schedule.repository.BatchJobRepository;
import com.favorsoft.schedule.service.BatchJobService;

@Service
public class BatchJobServiceImpl implements BatchJobService {
	
	@Autowired
	private BatchJobRepository batchJobRepository;

	@Override 
	public BatchJob getBatchJob(String jobName, String jobGroup) {
		return batchJobRepository.findByJobNameAndJobGroup(jobName, jobGroup);
	}

}
