package com.favorsoft.helper.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.helper.entity.Project;
import com.favorsoft.helper.entity.ProjectShift;

@Repository
public interface ProjectShiftRepository extends JpaRepository<ProjectShift, String> {
	
	List<ProjectShift> findByProject(Project project);
	
	List<ProjectShift> findByProjectAndStatus(Project project, String status);
	
	ProjectShift findByProjectAndHelpDate(Project project, Date helpDate);
	
	List<ProjectShift> findByProjectAndHelpDateBetween(Project project, Date startDate, Date endDate);
	
	List<ProjectShift> findByHelpers_knoxId(String knoxId);

}
