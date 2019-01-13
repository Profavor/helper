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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.favorsoft.shared.entity.BaseEntity;

@Entity
@Table(name="project_shift", uniqueConstraints=@UniqueConstraint(columnNames= {"project_id", "help_date"}) )
public class ProjectShift extends BaseEntity{
	
	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id", length = 128)
    private String id;
	
	@JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, targetEntity=Project.class)
    @JoinColumn(name="project_id", referencedColumnName = "id")
    private Project project;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	@Column(name="help_date")
	private Date helpDate;
	
	private String status;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "project_shift_helpers",
    joinColumns = @JoinColumn(name = "project_shift_id"),
    inverseJoinColumns = @JoinColumn(name = "helper_id"),
    uniqueConstraints= @UniqueConstraint(columnNames= {"project_shift_id", "helper_id"}))
	private List<Helper> helpers = new ArrayList<Helper>();
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ShiftHelperRequest> requests = new ArrayList<ShiftHelperRequest>();
	
	@Transient
    private String projectId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Date getHelpDate() {
		return helpDate;
	}

	public void setHelpDate(Date helpDate) {
		this.helpDate = helpDate;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<ShiftHelperRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<ShiftHelperRequest> requests) {
		this.requests = requests;
	}
	
}
