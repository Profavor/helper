package com.favorsoft.schedule.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="batchjob", uniqueConstraints={
		   @UniqueConstraint(columnNames={"jobName", "jobGroup"})
		})
public class BatchJob extends BaseEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BatchJob(String jobName, String jobGroup) {
		this.jobName = jobName;
		this.jobGroup = jobGroup;
	}
	
	public BatchJob() {
		
	}

	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
    private String id;
	
	@Column(length = 50)
	private String jobName;
	
	@Column(length = 50)
	private String jobGroup = "One";
	
	private String className;
	
	private String queueType;
	
	private String status;
	
	@JsonManagedReference
	@OneToMany(mappedBy="batchJob", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<BatchJobTrigger> batchJobTriggers = new ArrayList<BatchJobTrigger>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="batchJob", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<BatchJobParams> batchJobParams = new ArrayList<BatchJobParams>();
	
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<BatchJobTrigger> getBatchJobTriggers() {
		return batchJobTriggers;
	}

	public void setBatchJobTriggers(List<BatchJobTrigger> batchJobTriggers) {
		this.batchJobTriggers = batchJobTriggers;
	}

	public List<BatchJobParams> getBatchJobParams() {
		return batchJobParams;
	}

	public void setBatchJobParams(List<BatchJobParams> batchJobParams) {
		this.batchJobParams = batchJobParams;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
