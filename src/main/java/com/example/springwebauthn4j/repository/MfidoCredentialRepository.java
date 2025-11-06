package com.example.springwebauthn4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MfidoCredentialRepository extends JpaRepository<MfidoCredentialforWebAuthn4J, Integer> {
    List<MfidoCredentialforWebAuthn4J> findByUserInternalId(String userInternalId);
}