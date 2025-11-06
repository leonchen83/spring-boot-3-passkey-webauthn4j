package com.example.springwebauthn4j.service;

public interface WebAuthnServerService {
    RegisterOption getRegisterOption(String userId);

    AttestationVerifyResult verifyRegisterAttestation(
            RegisterOption registerOption,
            String publicKeyCredentialCreateResultJson
    );

    AuthenticateOption getAuthenticateOption();

    AssertionVerifyResult verifyAuthenticateAssertion(
            AuthenticateOption authenticateOption,
            String publicKeyCredentialGetResultJson
    );

    String toUserInternalId(String encodedUserHandle);
}