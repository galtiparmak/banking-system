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
@Table(name = "credit_cards")
public class CreditCard extends Card {
    private double creditLimit;
    private double debt;
    private double availableCredit;
    private double minimumPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public double getMinimumPayment() {
        return debt / 5;
    }

    public double getAvailableCredit() {
        return creditLimit - debt;
    }

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
