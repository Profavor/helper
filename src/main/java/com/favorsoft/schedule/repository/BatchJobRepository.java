package com.favorsoft.schedule.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.schedule.entity.BatchJob;

@Repository
public interface BatchJobRepository extends JpaRepository<BatchJob, String> {
	
	public BatchJob findByJobNameAndJobGroup(String jobName, String jobGroup);
}
