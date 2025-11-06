package com.example.springwebauthn4j.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MfidoCredentialRepository extends JpaRepository<MfidoCredentialforWebAuthn4J, Integer> {
	List<MfidoCredentialforWebAuthn4J> findByUserInternalId(String userInternalId);
}