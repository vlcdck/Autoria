package com.autoria.repository;

import com.autoria.models.token.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
    Optional<ConfirmationToken> findByToken(String token);
}
