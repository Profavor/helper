package com.favorsoft.helper.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="helper_change_response")
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class)
public class HelperChangeResponse extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
	private String id;

	@JsonBackReference("helperChangeResponses")
	@ManyToOne(cascade = CascadeType.ALL, targetEntity=Helper.class)
    @JoinColumn(name="helper_id", referencedColumnName = "id", nullable=false)
	private Helper helper;
	
	private Date responseDate;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity=ProjectShift.class)
    @JoinColumn(name="project_shift_id", referencedColumnName = "id", nullable=false)
	private ProjectShift projectShift;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity=Helper.class)
    @JoinColumn(name="helper_change_id", referencedColumnName = "id", nullable=false)
	private Helper changeHelper;
	
	private String status;	
	
	private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Helper getHelper() {
		return helper;
	}

	public void setHelper(Helper helper) {
		this.helper = helper;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	public Helper getChangeHelper() {
		return changeHelper;
	}

	public void setChangeHelper(Helper changeHelper) {
		this.changeHelper = changeHelper;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProjectShift getProjectShift() {
		return projectShift;
	}

	public void setProjectShift(ProjectShift projectShift) {
		this.projectShift = projectShift;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}		
}
