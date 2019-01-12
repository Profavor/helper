package com.favorsoft.schedule.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="batchjob_history")
public class BatchJobHistory extends BaseEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id   
    @Column(name = "id", length = 128)
    private String id;
    
    private String className;
    
    private Date startExecuteDate;
    
    private Date endExecuteDate;
    
    private String status;
    
    private String jobParams;
    
    private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Date getStartExecuteDate() {
		return startExecuteDate;
	}

	public void setStartExecuteDate(Date startExecuteDate) {
		this.startExecuteDate = startExecuteDate;
	}

	public Date getEndExecuteDate() {
		return endExecuteDate;
	}

	public void setEndExecuteDate(Date endExecuteDate) {
		this.endExecuteDate = endExecuteDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJobParams() {
		return jobParams;
	}

	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
