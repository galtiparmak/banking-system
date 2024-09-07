package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardNumber(String cardNumber);
}
