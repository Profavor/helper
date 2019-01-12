package com.favorsoft.shared.model;

public class ResponseModel {
	
	private boolean success;
	
	private String message;
	
	private Object results;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResults() {
		return results;
	}

	public void setResults(Object results) {
		this.results = results;
	}	
	
}
