package com.example.springwebauthn4j.controller;

import java.util.List;

import com.example.springwebauthn4j.service.AuthenticateOption;
import com.example.springwebauthn4j.service.Status;
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientInput;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;

public class ServerPublicKeyCredentialGetOptionsResponse extends ServerResponse {
	
	private final List<PublicKeyCredentialDescriptor> allowCredentials;
	private final String challenge;
	private final Long timeout;
	private final String rpId;
	private final UserVerificationRequirement userVerification;
	private final AuthenticationExtensionsClientInputs<AuthenticationExtensionClientInput> extensions;
	
	public ServerPublicKeyCredentialGetOptionsResponse(
			List<PublicKeyCredentialDescriptor> allowCredentials,
			String challenge,
			Long timeout,
			String rpId,
			UserVerificationRequirement userVerification,
			AuthenticationExtensionsClientInputs<AuthenticationExtensionClientInput> extensions
	) {
		super(Status.OK, "");
		this.allowCredentials = allowCredentials;
		this.challenge = challenge;
		this.timeout = timeout;
		this.rpId = rpId;
		this.userVerification = userVerification;
		this.extensions = extensions;
	}
	
	public ServerPublicKeyCredentialGetOptionsResponse(Status status, String errorMessage) {
		super(status, errorMessage);
		this.allowCredentials = null;
		this.challenge = null;
		this.timeout = null;
		this.rpId = null;
		this.userVerification = null;
		this.extensions = null;
	}
	
	public ServerPublicKeyCredentialGetOptionsResponse(AuthenticateOption authenticateOption) {
		super(Status.OK, "");
		var options = authenticateOption.getPublicKeyCredentialRequestOptions();
		this.allowCredentials = options.getAllowCredentials();
		this.challenge = options.getChallenge() == null ? null : options.getChallenge().toString();
		this.timeout = options.getTimeout();
		this.rpId = options.getRpId();
		this.userVerification = options.getUserVerification();
		this.extensions = options.getExtensions();
	}
	
	public List<PublicKeyCredentialDescriptor> getAllowCredentials() {
		return allowCredentials;
	}
	
	public String getChallenge() {
		return challenge;
	}
	
	public Long getTimeout() {
		return timeout;
	}
	
	public String getRpId() {
		return rpId;
	}
	
	public UserVerificationRequirement getUserVerification() {
		return userVerification;
	}
	
	public AuthenticationExtensionsClientInputs<AuthenticationExtensionClientInput> getExtensions() {
		return extensions;
	}
}