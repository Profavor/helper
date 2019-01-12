package com.favorsoft.schedule.model;

import java.util.Date;

public class LogRecord {
	public enum LogType {
		INFO, WARN, ERROR;
	}

	private Date date;
	private LogType type;

	private String message;
	private String threadName;

	public LogRecord(LogType type, String msg) {
		super();
		this.type = type;
		message = msg;
		date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	
}
