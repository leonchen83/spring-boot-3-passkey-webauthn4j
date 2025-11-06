package com.example.springwebauthn4j.service;

import com.webauthn4j.credential.CredentialRecord;
import org.springframework.data.util.Pair;

public interface FidoCredentialService {
    void save(String userId, AttestationVerifyResult attestationVerifyResult);
    Pair<CredentialRecord, String> load(String userInternalId, byte[] credentialId);
}