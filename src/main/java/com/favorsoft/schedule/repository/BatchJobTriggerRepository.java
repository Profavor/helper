package com.favorsoft.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.schedule.entity.BatchJobTrigger;

@Repository
public interface BatchJobTriggerRepository extends JpaRepository<BatchJobTrigger, String> {
	
	public BatchJobTrigger findByTriggerNameAndTriggerGroup(String triggerName, String triggerGroup);
}
