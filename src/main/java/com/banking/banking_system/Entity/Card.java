package com.banking.banking_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class Card {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private String cardPassword;

    private boolean isActive;

}
