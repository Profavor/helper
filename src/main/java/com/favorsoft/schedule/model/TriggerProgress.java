package com.favorsoft.schedule.model;

import java.util.Date;

public class TriggerProgress {

	private int timesTriggered;

	private int repeatCount;

	private Date finalFireTime;

	private Date nextFireTime;

	private Date previousFireTime;

	private String jobKey;

	private String jobClass;

	public int getPercentage() {
		if (this.repeatCount <= 0)
			return -1;
		return Math.round((float) timesTriggered / (float) this.repeatCount * 100);
	}

	public int getTimesTriggered() {
		return timesTriggered;
	}

	public void setTimesTriggered(int timesTriggered) {
		this.timesTriggered = timesTriggered;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Date getFinalFireTime() {
		return finalFireTime;
	}

	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	
	
}
