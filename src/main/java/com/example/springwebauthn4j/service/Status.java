package com.example.springwebauthn4j.service;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
	OK("ok"),
	FAILED("failed");
	
	private final String value;
	
	Status(String value) {
		this.value = value;
	}
	
	@JsonValue
	public String getValue() {
		return value;
	}
}