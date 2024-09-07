package com.banking.banking_system.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    private String cardType;
    private boolean isActive;
}
