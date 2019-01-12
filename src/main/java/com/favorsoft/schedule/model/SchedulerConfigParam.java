package com.favorsoft.schedule.model;

public class SchedulerConfigParam {
	public long triggerPerDay;
	public int maxCount;

	public SchedulerConfigParam() {
		super();
	}

	public SchedulerConfigParam(long triggerPerDay, int maxCount) {
		super();
		this.triggerPerDay = triggerPerDay;
		this.maxCount = maxCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public long getTriggerPerDay() {
		return triggerPerDay;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setTriggerPerDay(long triggerPerDay) {
		this.triggerPerDay = triggerPerDay;
	}

	@Override
	public String toString() {
		return "SchedulerConfigParam [triggerPerDay=" + triggerPerDay + ", maxCount=" + maxCount + "]";
	}
}
