package com.example.springwebauthn4j.service;

import com.webauthn4j.data.PublicKeyCredentialCreationOptions;

public class RegisterOption {
	
	private PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
	
	public RegisterOption() {
	}
	
	public RegisterOption(PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions) {
		this.publicKeyCredentialCreationOptions = publicKeyCredentialCreationOptions;
	}
	
	public PublicKeyCredentialCreationOptions getPublicKeyCredentialCreationOptions() {
		return publicKeyCredentialCreationOptions;
	}
	
	public void setPublicKeyCredentialCreationOptions(PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions) {
		this.publicKeyCredentialCreationOptions = publicKeyCredentialCreationOptions;
	}
}