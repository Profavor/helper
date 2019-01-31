package com.favorsoft.helper.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="project")
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class)
public class Project extends BaseEntity{
	
	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
    private String id;
	
	@Column(length = 128, nullable=false, unique=true)
	private String projectName;
	
	@Column(length = 2000)
	private String description;
	
	@Column(nullable=false)
	private int maxHelperCount;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date startDate;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	
	private String status;
	
	private String owner;
	
	private String triggerValue;
	
	private String educationUrl;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "project_helpers",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "helper_id"),
    uniqueConstraints= @UniqueConstraint(columnNames= {"project_id", "helper_id"}))
	private List<Helper> helpers = new ArrayList<Helper>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxHelperCount() {
		return maxHelperCount;
	}

	public void setMaxHelperCount(int maxHelperCount) {
		this.maxHelperCount = maxHelperCount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getTriggerValue() {
		return triggerValue;
	}

	public void setTriggerValue(String triggerValue) {
		this.triggerValue = triggerValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Helper> getHelpers() {
		return helpers;
	}

	public void setHelpers(List<Helper> helpers) {
		this.helpers = helpers;
	}

	public String getEducationUrl() {
		return educationUrl;
	}

	public void setEducationUrl(String educationUrl) {
		this.educationUrl = educationUrl;
	}	
}
