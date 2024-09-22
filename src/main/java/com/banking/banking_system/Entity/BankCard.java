package com.banking.banking_system.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "bank_cards")
public class BankCard extends Card {
    private double balance;
    @Column(unique = true, nullable = false)
    private String iban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public void setPassword(String password) {
        super.setCardPassword(password);
    }
    public String getPassword() {
        return super.getCardPassword();
    }

    public void setIsActive(boolean isActive) {
        super.setActive(isActive);
    }
}
