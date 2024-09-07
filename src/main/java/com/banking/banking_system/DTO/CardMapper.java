package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Entity.CreditCard;

public class CardMapper {

    public static CardDTO toDTO(CreditCard creditCard) {
        return new CardDTO(creditCard.getCardNumber(), creditCard.getExpirationDate(), creditCard.getCvv(), "CreditCard", creditCard.isActive());
    }

    public static CardDTO toDTO(BankCard bankCard) {
        return new CardDTO(bankCard.getCardNumber(), bankCard.getExpirationDate(), bankCard.getCvv(), "BankCard", bankCard.isActive());
    }

}

