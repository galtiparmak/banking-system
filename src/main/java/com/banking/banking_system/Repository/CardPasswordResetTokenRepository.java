package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.CardPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardPasswordResetTokenRepository extends JpaRepository<CardPasswordResetToken, Long> {
    Optional<CardPasswordResetToken> findByToken(String token);
}
