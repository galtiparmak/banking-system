package com.banking.banking_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card_password_reset_tokens")
public class CardPasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_card_id", nullable = true)
    private CreditCard creditCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_card_id", nullable = true)
    private BankCard bankCard;

    @Column(nullable = false)
    private Date expiryDate;

    public CardPasswordResetToken(String token, CreditCard creditCard, Date expiryDate) {
        this.token = token;
        this.creditCard = creditCard;
        this.expiryDate = expiryDate;
    }
    public CardPasswordResetToken(String token, BankCard bankCard, Date expiryDate) {
        this.token = token;
        this.bankCard = bankCard;
        this.expiryDate = expiryDate;
    }
}


