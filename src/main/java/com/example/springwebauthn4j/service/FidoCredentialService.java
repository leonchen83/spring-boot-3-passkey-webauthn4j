package com.example.springwebauthn4j.service;

import org.springframework.data.util.Pair;

import com.webauthn4j.credential.CredentialRecord;

public interface FidoCredentialService {
	void save(String userId, AttestationVerifyResult attestationVerifyResult);
	
	Pair<CredentialRecord, String> load(String userInternalId, byte[] credentialId);
}