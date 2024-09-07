package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    public Optional<BankCard> findByCardNumber(String cardNumber);
    public Optional<BankCard> findByIban(String iban);
}
