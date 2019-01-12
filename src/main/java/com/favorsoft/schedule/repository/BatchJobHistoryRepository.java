package com.favorsoft.schedule.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.schedule.entity.BatchJobHistory;

@Repository
public interface BatchJobHistoryRepository extends JpaRepository<BatchJobHistory, String> {

}
