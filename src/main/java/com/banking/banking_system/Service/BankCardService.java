package com.banking.banking_system.Service;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.DTO.CardMapper;
import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Entity.CreditCard;
import com.banking.banking_system.Repository.BankCardRepository;
import com.banking.banking_system.Repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankCardService {
    // implement money transaction from one bank card to another
    // buy something with bank card

    private BankCardRepository bankCardRepository;
    private CreditCardRepository creditCardRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public BankCardService(BankCardRepository bankCardRepository, CreditCardRepository creditCardRepository, PasswordEncoder passwordEncoder) {
        this.bankCardRepository = bankCardRepository;
        this.creditCardRepository = creditCardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CardDTO getBankCard(String cardNumber, String password) {
        BankCard bankCard = getCardHelper(cardNumber, password);
        return CardMapper.toDTO(bankCard);
    }

    public boolean spendMoney(String cardNumber, String password, double amount) {
        BankCard bankCard = getCardHelper(cardNumber, password);
        if (bankCard == null) {
            return false;
        }

        if (bankCard.getBalance() >= amount) {
            bankCard.setBalance(bankCard.getBalance() - amount);
            bankCardRepository.save(bankCard);
            return true;
        }

        return false;
    }

    public boolean moneyTransaction(String cardNumber, String password, String receiverIban, double amount) {
        BankCard bankCard = getCardHelper(cardNumber, password);
        if (bankCard == null) {
            return false;
        }

        Optional<BankCard> optionalReceiverBankCard = bankCardRepository.findByIban(receiverIban);
        if (optionalReceiverBankCard.isEmpty()) {
            throw new RuntimeException("Receiver bank card not found");
        }
        BankCard receiverBankCard = optionalReceiverBankCard.get();

        if (bankCard.getBalance() >= amount) {
            bankCard.setBalance(bankCard.getBalance() - amount);
            receiverBankCard.setBalance(receiverBankCard.getBalance() + amount);
            bankCardRepository.save(bankCard);
            bankCardRepository.save(receiverBankCard);
            return true;
        }
        return false;
    }

    public boolean payDebt(String cardNumber, String password, String creditCardNumber, double amount) {
        BankCard bankCard = getCardHelper(cardNumber, password);
        if (bankCard == null) {
            return false;
        }

        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(creditCardNumber);
        if (optionalCreditCard.isEmpty()) {
            throw new RuntimeException("Credit card not found");
        }
        CreditCard creditCard = optionalCreditCard.get();

        if (bankCard.getBalance() >= amount && bankCard.getUser().equals(creditCard.getUser()) && creditCard.getDebt() >= amount) {
            bankCard.setBalance(bankCard.getBalance() - amount);
            creditCard.setDebt(creditCard.getDebt() - amount);
            bankCardRepository.save(bankCard);
            creditCardRepository.save(creditCard);
            return true;
        }

        return false;
    }

    public BankCard getCardHelper(String cardNumber, String password) {
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNumber);
        if (optionalBankCard.isEmpty()) {
            throw new RuntimeException("Bank card not found");
        }
        BankCard bankCard = optionalBankCard.get();

        if (passwordEncoder.matches(password, bankCard.getPassword())) {
            return bankCard;
        }
        return null;
    }
}
