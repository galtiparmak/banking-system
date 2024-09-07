package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    public Optional<Admin> findByUsername(String username);
}
