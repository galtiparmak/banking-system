package com.banking.banking_system.Controller;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Service.EmployeeService;
import com.banking.banking_system.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final MailService mailService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, MailService mailService) {
        this.employeeService = employeeService;
        this.mailService = mailService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(employeeService.getAllUsers());
    }

    @GetMapping("/credit-cards")
    public ResponseEntity<List<CreditCard>> getAllCreditCards() {
        return ResponseEntity.ok(employeeService.getAllCreditCards());
    }

    @GetMapping("/bank-cards")
    public ResponseEntity<List<BankCard>> getAllBankCards() {
        return ResponseEntity.ok(employeeService.getAllBankCards());
    }

    @GetMapping("/cards-of-user")
    public ResponseEntity<List<CardDTO>> getAllCardsOfUser(@RequestParam String TC) {
        return ResponseEntity.ok(employeeService.getAllCardsOfUser(TC));
    }

    @GetMapping("/card-requests")
    public ResponseEntity<List<CardRequest>> getAllCardRequests() {
        return ResponseEntity.ok(employeeService.getAllCardRequests());
    }

    @GetMapping("/card-requests/{id}")
    public ResponseEntity<CardRequest> getCardRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getCardRequestById(id));
    }

    @PostMapping("/approve-credit-card-request")
    public ResponseEntity<String> approveCreditCardRequest(@RequestParam Long id, @RequestParam double amount) {
        if (employeeService.approveCreditCardRequest(id, amount)) {
            return ResponseEntity.ok("Credit card request approved");
        }
        return ResponseEntity.badRequest().body("Credit card request not approved");
    }

    @PostMapping("/approve-bank-card-request")
    public ResponseEntity<String> approveBankCardRequest(@RequestParam Long id) {
        if (employeeService.approveBankCardRequest(id)) {
            return ResponseEntity.ok("Bank card request approved");
        }
        return ResponseEntity.badRequest().body("Bank card request not approved");
    }

    @PostMapping("/reset-card-password-request")
    public ResponseEntity<String> cardPasswordResetRequest(@RequestParam String cardNumber) {
        if (mailService.resetCardPasswordRequest(cardNumber)) {
            return ResponseEntity.ok("Card password reset request email sent");
        }
        return ResponseEntity.badRequest().body("Card password reset request email not sent");
    }

    @GetMapping("/get-card-password-reset-tokens")
    public ResponseEntity<List<CardPasswordResetToken>> getCardPasswordResetTokens() {
        return ResponseEntity.ok(employeeService.getAllCardPasswordResetTokens());
    }

}
