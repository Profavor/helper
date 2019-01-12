package com.favorsoft.helper.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.helper.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

	public Project findByProjectName(String projectName);
	
	public List<Project> findByStatus(String status);
}
