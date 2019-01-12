package com.favorsoft.schedule.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.schedule.entity.BatchJobParams;

@Repository
public interface BatchJobParamsRepository extends JpaRepository<BatchJobParams, String> {

}
