package com.example.springwebauthn4j.service;

public class AssertionVerifyResult {

    private boolean isSuccess;
    private String userId;

    public AssertionVerifyResult() {
    }

    public AssertionVerifyResult(boolean isSuccess, String userId) {
        this.isSuccess = isSuccess;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}