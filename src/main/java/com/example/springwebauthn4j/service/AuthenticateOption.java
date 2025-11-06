package com.example.springwebauthn4j.service;

import com.webauthn4j.data.PublicKeyCredentialRequestOptions;

public class AuthenticateOption {

    private PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;

    public AuthenticateOption() {
    }

    public AuthenticateOption(PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions) {
        this.publicKeyCredentialRequestOptions = publicKeyCredentialRequestOptions;
    }

    public PublicKeyCredentialRequestOptions getPublicKeyCredentialRequestOptions() {
        return publicKeyCredentialRequestOptions;
    }

    public void setPublicKeyCredentialRequestOptions(PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions) {
        this.publicKeyCredentialRequestOptions = publicKeyCredentialRequestOptions;
    }
}