package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.UserPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserPasswordResetTokenRepository extends JpaRepository<UserPasswordResetToken, Long> {
    Optional<UserPasswordResetToken> findByToken(String token);
}
