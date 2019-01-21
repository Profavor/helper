package com.favorsoft.schedule.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="batchjob_trigger", uniqueConstraints={
		   @UniqueConstraint(columnNames={"triggerName", "triggerGroup"})
		})
public class BatchJobTrigger extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BatchJobTrigger() {}
	
	public BatchJobTrigger(String triggerName, String triggerGroup, String triggerValue) {
		this.triggerName = triggerName;
		this.triggerGroup = triggerGroup;
		this.triggerValue = triggerValue;
	}

	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
    private String id;
    
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, targetEntity=BatchJob.class)
    @JoinColumn(name="batchjob_id", referencedColumnName = "id")
    private BatchJob batchJob;
    
    @Column(length = 50)
    private String triggerName;
    
    @Column(length = 50)
    private String triggerGroup = "TONE";
    
    private String triggerValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BatchJob getBatchJob() {
		return batchJob;
	}

	public void setBatchJob(BatchJob batchJob) {
		this.batchJob = batchJob;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public String getTriggerValue() {
		return triggerValue;
	}

	public void setTriggerValue(String triggerValue) {
		this.triggerValue = triggerValue;
	}   
    
}
