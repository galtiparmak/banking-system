package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.ATM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ATMRepository extends JpaRepository<ATM, Long> {
    Optional<ATM> findById(Long id);
}
