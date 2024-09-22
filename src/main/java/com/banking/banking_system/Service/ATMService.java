package com.banking.banking_system.Service;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.DTO.CardMapper;
import com.banking.banking_system.Entity.ATM;
import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Entity.Card;
import com.banking.banking_system.Entity.CreditCard;
import com.banking.banking_system.Repository.ATMRepository;
import com.banking.banking_system.Repository.BankCardRepository;
import com.banking.banking_system.Repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ATMService {
    private final ATMRepository atmRepository;
    private final BankCardRepository bankCardRepository;
    private final CreditCardRepository creditCardRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public ATMService(ATMRepository atmRepository,
                      BankCardRepository bankCardRepository,
                      PasswordEncoder passwordEncoder,
                      CreditCardRepository creditCardRepository) {
        this.atmRepository = atmRepository;
        this.bankCardRepository = bankCardRepository;
        this.passwordEncoder = passwordEncoder;
        this.creditCardRepository = creditCardRepository;
    }

    public ATM getATM(Long id) {
        Optional<ATM> optionalATM = atmRepository.findById(id);
        if (optionalATM.isEmpty()) {
            throw new RuntimeException("ATM not found");
        }
        return optionalATM.get();
    }

    public List<ATM> getATMs() {
        return atmRepository.findAll();
    }

    public boolean createATM(String location, double balance) {
        try {
            ATM atm = ATM.builder()
                    .location(location)
                    .balance(balance)
                    .build();
            atmRepository.save(atm);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteATM(Long id) {
        try {
            atmRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CardDTO getCard(String cardNumber, String password, Class<? extends Card> cardType) {
        Optional<? extends Card> optionalCard;

        if (cardType.equals(BankCard.class)) {
            optionalCard = bankCardRepository.findByCardNumber(cardNumber);
        } else if (cardType.equals(CreditCard.class)) {
            optionalCard = creditCardRepository.findByCardNumber(cardNumber);
        } else {
            throw new IllegalArgumentException("Unknown card type");
        }

        if (optionalCard.isEmpty()) {
            throw new RuntimeException("Card not found");
        }

        Card card = optionalCard.get();
        if (isCardActiveAndPasswordMatches(card, password)) {
            throw new RuntimeException("Card is not active or password is incorrect");
        }

        CardDTO cardDTO = CardMapper.toDTO(card);
        cardDTO.setCardType(cardType.getSimpleName());

        return cardDTO;
    }

    @Transactional
    public boolean changeCardPassword(String cardNumber, String oldPassword, String newPassword, Class<? extends Card> cardType) {
        Optional<? extends Card> optionalCard;

        if (cardType.equals(BankCard.class)) {
            optionalCard = bankCardRepository.findByCardNumber(cardNumber);
        } else if (cardType.equals(CreditCard.class)) {
            optionalCard = creditCardRepository.findByCardNumber(cardNumber);
        } else {
            throw new IllegalArgumentException("Unknown card type");
        }

        if (optionalCard.isEmpty()) {
            return false;
        }

        Card card = optionalCard.get();

        if (isCardActiveAndPasswordMatches(card, oldPassword)) {
            return false;
        }

        card.setCardPassword(passwordEncoder.encode(newPassword));

        if (card instanceof BankCard) {
            bankCardRepository.save((BankCard) card);
        } else if (card instanceof CreditCard) {
            creditCardRepository.save((CreditCard) card);
        }

        return true;
    }


    @Transactional
    public boolean depositMoney(Long atmId, String cardNumber, String password, double amount) {
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNumber);
        if (optionalBankCard.isEmpty()) {
            return false;
        }

        BankCard bankCard = optionalBankCard.get();

        if (isCardActiveAndPasswordMatches(bankCard, password)) {
            return false;
        }
        ATM atm = getATM(atmId);
        if (bankCard.getBalance() < amount) {
            return false;
        }

        atm.setBalance(atm.getBalance() + amount);
        bankCard.setBalance(bankCard.getBalance() - amount);
        atmRepository.save(atm);
        bankCardRepository.save(bankCard);

        return true;
    }

    @Transactional
    public boolean withdrawMoney(Long atmID, String cardNumber, String password, double amount) {
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNumber);
        if (optionalBankCard.isEmpty()) {
            return false;
        }
        BankCard bankCard = optionalBankCard.get();

        if (isCardActiveAndPasswordMatches(bankCard, password)) {
            return false;
        }

        ATM atm = getATM(atmID);
        if (atm.getBalance() < amount) {
            return false;
        }
        atm.setBalance(atm.getBalance() - amount);
        bankCard.setBalance(bankCard.getBalance() + amount);
        atmRepository.save(atm);
        bankCardRepository.save(bankCard);

        return true;
    }

    public double getBalance(String cardNumber, String password) {
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNumber);
        if (optionalBankCard.isEmpty()) {
            return -1;
        }
        BankCard bankCard = optionalBankCard.get();

        if (isCardActiveAndPasswordMatches(bankCard, password)) {
            throw new RuntimeException("Card is not active or password is incorrect");
        }

        return bankCard.getBalance();
    }


    @Transactional
    public boolean payDebtOfCreditCardWithBankCard(String bankCardNumber, String creditCardNumber, String bankCardPassword, double amount) {
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(bankCardNumber);
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(creditCardNumber);
        if (optionalBankCard.isEmpty() || optionalCreditCard.isEmpty()) {
            return false;
        }

        BankCard bankCard = optionalBankCard.get();
        CreditCard creditCard = optionalCreditCard.get();

        if (!bankCard.getUser().equals(creditCard.getUser())) {
            return false;
        }

        if (isCardActiveAndPasswordMatches(bankCard, bankCardPassword)) {
            return false;
        }

        if (bankCard.getBalance() < amount) {
            return false;
        }

        bankCard.setBalance(bankCard.getBalance() - amount);
        creditCard.setDebt(creditCard.getDebt() - amount);
        bankCardRepository.save(bankCard);
        creditCardRepository.save(creditCard);

        return true;
    }

    public boolean isCardActiveAndPasswordMatches(Card card, String password) {
        return !(card.isActive() && passwordEncoder.matches(password, card.getCardPassword()));
    }
}
