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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="helper_change_response", uniqueConstraints=@UniqueConstraint(columnNames= {"help_date", "helper_change_id"}))
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
	
	@JsonFormat(pattern="yyyy-MM-dd")
	@Column(name = "help_date")
	private Date helpDate;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity=Helper.class)
    @JoinColumn(name="helper_change_id", referencedColumnName = "id", nullable=false)
	private Helper changeHelper;
	
	private String status;	

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

	public Date getHelpDate() {
		return helpDate;
	}

	public void setHelpDate(Date helpDate) {
		this.helpDate = helpDate;
	}	
}
