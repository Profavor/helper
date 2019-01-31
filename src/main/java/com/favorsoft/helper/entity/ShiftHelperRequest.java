package com.favorsoft.helper.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="shift_helper_request", uniqueConstraints=@UniqueConstraint(columnNames= {"id", "helper_id"}))
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class)
public class ShiftHelperRequest extends BaseEntity{
	
	public ShiftHelperRequest() {}
	
	public ShiftHelperRequest(ProjectShift projectShift, Helper helper) {
		this.projectShift = projectShift;
		this.helper = helper;
	}
	
	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
    private String id;
	
	@JsonBackReference("requests")
    @ManyToOne(cascade = CascadeType.ALL, targetEntity=ProjectShift.class)
    @JoinColumn(name="project_shift_id", referencedColumnName = "id")
    private ProjectShift projectShift;
	
	private Date requestDate;
	
	@OneToOne(cascade = CascadeType.ALL, targetEntity=Helper.class)
	@JoinColumn(name="helper_id", referencedColumnName = "id")
	private Helper helper;
	
	private String status;
	
	@Transient
    private String projectShiftId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProjectShift getProjectShift() {
		return projectShift;
	}

	public void setProjectShift(ProjectShift projectShift) {
		this.projectShift = projectShift;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Helper getHelper() {
		return helper;
	}

	public void setHelper(Helper helper) {
		this.helper = helper;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProjectShiftId() {
		return projectShiftId;
	}

	public void setProjectShiftId(String projectShiftId) {
		this.projectShiftId = projectShiftId;
	}	
}
