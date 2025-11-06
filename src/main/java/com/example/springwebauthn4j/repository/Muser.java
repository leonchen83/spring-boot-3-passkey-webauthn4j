package com.example.springwebauthn4j.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "M_USER")
public class Muser {

    @Id
    @Column(name = "INTERNAL_ID")
    private String internalId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "PASSWORD")
    private String password;

    public Muser() {
    }

    public Muser(String internalId, String userId, String displayName, String password) {
        this.internalId = internalId;
        this.userId = userId;
        this.displayName = displayName;
        this.password = password;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}