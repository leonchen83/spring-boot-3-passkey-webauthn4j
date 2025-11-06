package com.example.springwebauthn4j.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "M_FIDO_CREDENTIAL_FOR_WEBAUTHN4J")
public class MfidoCredentialforWebAuthn4J {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "USER_INTERNAL_ID")
	private String userInternalId;
	
	@Column(name = "CREDENTIAL_ID")
	private byte[] credentialId;
	
	@Column(name = "SIGN_COUNT")
	private Long signCount;
	
	@Column(name = "ATTESTED_CREDENTIAL_DATA")
	private byte[] ateestedCredentialData;
	
	public MfidoCredentialforWebAuthn4J() {
	}
	
	public MfidoCredentialforWebAuthn4J(Integer id, String userInternalId, byte[] credentialId, Long signCount, byte[] ateestedCredentialData) {
		this.id = id;
		this.userInternalId = userInternalId;
		this.credentialId = credentialId;
		this.signCount = signCount;
		this.ateestedCredentialData = ateestedCredentialData;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUserInternalId() {
		return userInternalId;
	}
	
	public void setUserInternalId(String userInternalId) {
		this.userInternalId = userInternalId;
	}
	
	public byte[] getCredentialId() {
		return credentialId;
	}
	
	public void setCredentialId(byte[] credentialId) {
		this.credentialId = credentialId;
	}
	
	public Long getSignCount() {
		return signCount;
	}
	
	public void setSignCount(Long signCount) {
		this.signCount = signCount;
	}
	
	public byte[] getAteestedCredentialData() {
		return ateestedCredentialData;
	}
	
	public void setAteestedCredentialData(byte[] ateestedCredentialData) {
		this.ateestedCredentialData = ateestedCredentialData;
	}
}