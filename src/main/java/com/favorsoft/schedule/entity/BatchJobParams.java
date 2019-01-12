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
@Table(name="batchjob_params", uniqueConstraints={
		   @UniqueConstraint(columnNames={"batchjob_id", "paramKey"})
		})
public class BatchJobParams extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
    private String paramKey;
    
    @Column(length = 100)
    private String paramValue;

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

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
