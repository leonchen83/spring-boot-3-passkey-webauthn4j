package com.example.springwebauthn4j.service;

import com.webauthn4j.credential.CredentialRecord;

public class AttestationVerifyResult {

    private byte[] credentialId;
    private CredentialRecord credentialRecord;

    public AttestationVerifyResult() {
    }

    public AttestationVerifyResult(byte[] credentialId, CredentialRecord credentialRecord) {
        this.credentialId = credentialId;
        this.credentialRecord = credentialRecord;
    }

    public byte[] getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(byte[] credentialId) {
        this.credentialId = credentialId;
    }

    public CredentialRecord getCredentialRecord() {
        return credentialRecord;
    }

    public void setCredentialRecord(CredentialRecord credentialRecord) {
        this.credentialRecord = credentialRecord;
    }
}