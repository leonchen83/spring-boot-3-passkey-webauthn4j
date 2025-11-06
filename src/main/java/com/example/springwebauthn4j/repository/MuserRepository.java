package com.example.springwebauthn4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MuserRepository extends JpaRepository<Muser, String> {
    Muser findByInternalId(String internalId);
    Muser findByUserId(String userId);
}