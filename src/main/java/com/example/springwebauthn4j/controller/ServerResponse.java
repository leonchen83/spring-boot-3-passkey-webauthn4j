package com.example.springwebauthn4j.controller;

import com.example.springwebauthn4j.service.Status;

public class ServerResponse {
	private Status status;
	private String errorMessage;
	
	public ServerResponse(Status status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}