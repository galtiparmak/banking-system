package com.banking.banking_system.Service;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.DTO.CardMapper;
import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.DTO.EmployeeMapper;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CardRequestRepository cardRequestRepository;
    private final CardPasswordResetTokenRepository cardPasswordResetTokenRepository;
    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankCardRepository bankCardRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int CARD_NUMBER_LENGTH = 16;
    private final int CVV_LENGTH = 3;

    @Autowired
    public EmployeeService(CardRequestRepository cardRequestRepository
            ,EmployeeRepository employeeRepository
            ,CardPasswordResetTokenRepository cardPasswordResetTokenRepository
            ,CreditCardRepository creditCardRepository
            ,BankCardRepository bankCardRepository
            ,UserRepository userRepository) {
        this.cardRequestRepository = cardRequestRepository;
        this.employeeRepository = employeeRepository;
        this.cardPasswordResetTokenRepository = cardPasswordResetTokenRepository;
        this.creditCardRepository = creditCardRepository;
        this.bankCardRepository = bankCardRepository;
        this.userRepository = userRepository;
    }

    public EmployeeDTO getEmployee(String TC) {
        Optional<Employee> optionalEmployee = employeeRepository.findByTC(TC);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        Employee employee = optionalEmployee.get();
        return EmployeeMapper.toDTO(employee);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<CreditCard> getAllCreditCards() {
        return creditCardRepository.findAll();
    }

    public List<BankCard> getAllBankCards() {
        return bankCardRepository.findAll();
    }

    public List<CardDTO> getAllCardsOfUser(String TC) {
        Optional<User> optionalUser = userRepository.findByTC(TC);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();

        List<CreditCard> creditCards  = user.getCreditCards();
        List<CardDTO> cardDTOS = new ArrayList<>();
        for (CreditCard creditCard : creditCards) {
            cardDTOS.add(CardMapper.toDTO(creditCard));
        }
        List<BankCard> bankCards = user.getBankCards();
        for (BankCard bankCard : bankCards) {
            cardDTOS.add(CardMapper.toDTO(bankCard));
        }
        return cardDTOS;
    }


    public List<CardRequest> getAllCardRequests() {
        return cardRequestRepository.findAll();
    }

    public CardRequest getCardRequestById(Long id) {
        Optional<CardRequest> optionalCardRequest = cardRequestRepository.findById(id);
        if (optionalCardRequest.isEmpty()) {
            throw new RuntimeException("Card request not found");
        }
        return optionalCardRequest.get();
    }

    public CreditCard generateCreditCard(User user, double creditLimit) {
        return CreditCard.builder()
                .cardNumber(generateUniqueCreditCardNumber())
                .expirationDate(generateExpirationDate())
                .cvv(generateCvv())
                .cardPassword("")
                .isActive(false)
                .creditLimit(creditLimit)
                .debt(0)
                .availableCredit(creditLimit)
                .user(user)
                .build();
    }

    public BankCard generateBankCard(User user) {
        return BankCard.builder()
                .cardNumber(generateUniqueBankCardNumber())
                .expirationDate(generateExpirationDate())
                .cvv(generateCvv())
                .cardPassword("")
                .isActive(false)
                .balance(0)
                .iban(generateUniqueIban())
                .user(user)
                .build();
    }


    @Transactional
    public boolean approveCreditCardRequest(Long cardRequestId, double creditLimit) {
        if (creditLimit < 0) {
            return false;
        }

        Optional<CardRequest> optionalCardRequest = cardRequestRepository.findById(cardRequestId);
        if (optionalCardRequest.isEmpty()) {
            return false;
        }
        CardRequest cardRequest = optionalCardRequest.get();

        Optional<User> optionalUser = userRepository.findByTC(cardRequest.getUserTC());
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        CreditCard creditCard = generateCreditCard(user, creditLimit);
        creditCardRepository.save(creditCard);

        return true;
    }

    @Transactional
    public boolean approveBankCardRequest(Long cardRequestId) {
        Optional<CardRequest> optionalCardRequest = cardRequestRepository.findById(cardRequestId);
        if (optionalCardRequest.isEmpty()) {
            return false;
        }
        CardRequest cardRequest = optionalCardRequest.get();

        Optional<User> optionalUser = userRepository.findByTC(cardRequest.getUserTC());
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        BankCard bankCard = generateBankCard(user);
        bankCardRepository.save(bankCard);

        return true;
    }

    public List<CardPasswordResetToken> getAllCardPasswordResetTokens() {
        return cardPasswordResetTokenRepository.findAll();
    }

    // Helpers

    public String generateUniqueCreditCardNumber() {
        int count = 0;
        String cardNum;

        while (true) {
            count++;
            cardNum = generateCardNumber();

            Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(cardNum);
            if (optionalCreditCard.isEmpty()) {
                break;
            }
            if (count > 100) {
                throw new RuntimeException("Cannot generate a unique card number");
            }
        }
        return cardNum;
    }

    public String generateUniqueBankCardNumber() {
        int count = 0;
        String cardNum;

        while (true) {
            count++;
            cardNum = generateCardNumber();

            Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNum);
            if (optionalBankCard.isEmpty()) {
                break;
            }
            if (count > 100) {
                throw new RuntimeException("Cannot generate a unique card number");
            }
        }
        return cardNum;
    }

    public String generateCardNumber() {
        StringBuilder sb = new StringBuilder(CARD_NUMBER_LENGTH);
        sb.append(2);
        sb.append(9);

        for (int i = 2; i < CARD_NUMBER_LENGTH; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    public String generateCvv() {
        StringBuilder sb = new StringBuilder(CVV_LENGTH);

        for (int i = 0; i < CVV_LENGTH; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    public String generateExpirationDate() {
        int month = secureRandom.nextInt(12) + 1;
        int year = secureRandom.nextInt(5) + 2024;
        return month + "/" + year;
    }

    public String generateUniqueIban() {
        int count = 0;
        String iban;

        while (true) {
            count++;
            iban = generateIban();

            Optional<BankCard> optionalBankCard = bankCardRepository.findByIban(iban);
            if (optionalBankCard.isEmpty()) {
                break;
            }
            if (count > 100) {
                throw new RuntimeException("Cannot generate a unique iban");
            }
        }
        return iban;
    }

    public String generateIban() {
        StringBuilder sb = new StringBuilder(26);
        sb.append("TR01");

        for (int i = 0; i < 24; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }
}
