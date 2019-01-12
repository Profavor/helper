package com.favorsoft.helper.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.helper.entity.ProjectShift;

@Repository
public interface ProjectShiftRepository extends JpaRepository<ProjectShift, String> {

}
