package com.favorsoft.shared.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	@CreatedBy
	@Column(name = "CREATOR", length = 128, nullable = true, updatable = false)
	private String creator = "BATCH_ADMIN";

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATEDDATE", nullable = false, updatable = false)
	private Date createdDate = new Date();
	
	@LastModifiedBy
	@Column(name = "UPDATOR", length = 128, nullable = true)
	private String updator = "BATCH_ADMIN";

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATED", nullable = true)
	private Date lastUpdated = new Date();

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
}
