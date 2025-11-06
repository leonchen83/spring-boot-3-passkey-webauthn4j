package com.example.springwebauthn4j.controller;

import java.util.List;

import com.example.springwebauthn4j.service.RegisterOption;
import com.example.springwebauthn4j.service.Status;
import com.webauthn4j.data.AttestationConveyancePreference;
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientInput;

public class ServerPublicKeyCredentialCreationOptionsResponse extends ServerResponse {
	
	private final PublicKeyCredentialRpEntity rp;
	private final PublicKeyCredentialUserEntity user;
	private final AttestationConveyancePreference attestation;
	private final AuthenticatorSelectionCriteria authenticatorSelection;
	private final String challenge;
	private final List<PublicKeyCredentialDescriptor> excludeCredentials;
	private final List<PublicKeyCredentialParameters> pubKeyCredParams;
	private final Long timeout;
	private final AuthenticationExtensionsClientInputs<RegistrationExtensionClientInput> extensions;
	
	public ServerPublicKeyCredentialCreationOptionsResponse(
			PublicKeyCredentialRpEntity rp,
			PublicKeyCredentialUserEntity user,
			AttestationConveyancePreference attestation,
			AuthenticatorSelectionCriteria authenticatorSelection,
			String challenge,
			List<PublicKeyCredentialDescriptor> excludeCredentials,
			List<PublicKeyCredentialParameters> pubKeyCredParams,
			Long timeout,
			AuthenticationExtensionsClientInputs<RegistrationExtensionClientInput> extensions
	) {
		super(Status.OK, "");
		this.rp = rp;
		this.user = user;
		this.attestation = attestation;
		this.authenticatorSelection = authenticatorSelection;
		this.challenge = challenge;
		this.excludeCredentials = excludeCredentials;
		this.pubKeyCredParams = pubKeyCredParams;
		this.timeout = timeout;
		this.extensions = extensions;
	}
	
	public ServerPublicKeyCredentialCreationOptionsResponse(Status status, String errorMessage) {
		super(status, errorMessage);
		this.rp = null;
		this.user = null;
		this.attestation = null;
		this.authenticatorSelection = null;
		this.challenge = null;
		this.excludeCredentials = null;
		this.pubKeyCredParams = null;
		this.timeout = null;
		this.extensions = null;
	}
	
	public ServerPublicKeyCredentialCreationOptionsResponse(RegisterOption registerOption) {
		super(Status.OK, "");
		var options = registerOption.getPublicKeyCredentialCreationOptions();
		this.rp = options.getRp();
		this.user = options.getUser();
		this.attestation = options.getAttestation();
		this.authenticatorSelection = options.getAuthenticatorSelection();
		this.challenge = options.getChallenge() == null ? null : options.getChallenge().toString();
		this.excludeCredentials = options.getExcludeCredentials();
		this.pubKeyCredParams = options.getPubKeyCredParams();
		this.timeout = options.getTimeout();
		this.extensions = options.getExtensions();
	}
	
	public PublicKeyCredentialRpEntity getRp() {
		return rp;
	}
	
	public PublicKeyCredentialUserEntity getUser() {
		return user;
	}
	
	public AttestationConveyancePreference getAttestation() {
		return attestation;
	}
	
	public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
		return authenticatorSelection;
	}
	
	public String getChallenge() {
		return challenge;
	}
	
	public List<PublicKeyCredentialDescriptor> getExcludeCredentials() {
		return excludeCredentials;
	}
	
	public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
		return pubKeyCredParams;
	}
	
	public Long getTimeout() {
		return timeout;
	}
	
	public AuthenticationExtensionsClientInputs<RegistrationExtensionClientInput> getExtensions() {
		return extensions;
	}
}