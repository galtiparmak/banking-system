package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.CardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {
    public Optional<CardRequest> findByUserTc(String userTc);
    public Optional<CardRequest> findByCardType(String cardType);
}
