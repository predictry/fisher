package com.predictry.fisher.domain;

public class ErrorMessage {

	private String error;
	
	public ErrorMessage() {}
	
	public ErrorMessage(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setStatus(String error) {
		this.error = error;
	}
	
}