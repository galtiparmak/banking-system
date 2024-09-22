package com.banking.banking_system.Controller;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Entity.CreditCard;
import com.banking.banking_system.Service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm/user")
@PreAuthorize("hasRole('USER')")
public class ATMUserController {

    private final ATMService atmService;
    @Autowired
    public ATMUserController(ATMService atmService) {
        this.atmService = atmService;
    }

    @GetMapping("/getBankCard")
    public ResponseEntity<CardDTO> getBankCard(@RequestParam String cardNumber, @RequestParam String password) {
        return ResponseEntity.ok(atmService.getCard(cardNumber, password, BankCard.class));
    } // http://localhost:8080/atm/user/getBankCard?id=...&password=...

    @GetMapping("/getCreditCard")
    public ResponseEntity<CardDTO> getCreditCard(@RequestParam String cardNumber, @RequestParam String password) {
        return ResponseEntity.ok(atmService.getCard(cardNumber, password, CreditCard.class));
    } // http://localhost:8080/atm/user/getCreditCard?id=...&password=...

    @PutMapping("/changePassword/BankCard")
    public ResponseEntity<String> changePasswordBankCard(@RequestParam String cardNumber, @RequestParam String oldPassword, @RequestParam String newPassword) {
        if (atmService.changeCardPassword(cardNumber, oldPassword, newPassword, BankCard.class)) {
            return ResponseEntity.ok("Password changed successfully");
        }
        return ResponseEntity.badRequest().body("Password change failed");
    } // http://localhost:8080/atm/user/changePassword/BankCard?id=...&oldPassword=...&newPassword=...

    @PutMapping("/changePassword/CreditCard")
    public ResponseEntity<String> changePasswordCreditCard(@RequestParam String cardNumber, @RequestParam String oldPassword, @RequestParam String newPassword) {
        if (atmService.changeCardPassword(cardNumber, oldPassword, newPassword, CreditCard.class)) {
            return ResponseEntity.ok("Password changed successfully");
        }
        return ResponseEntity.badRequest().body("Password change failed");
    } // http://localhost:8080/atm/user/changePassword/CreditCard?id=...&oldPassword=...&newPassword=...

    @PutMapping("/depositMoney")
    public ResponseEntity<String> depositMoney(@RequestParam Long id, @RequestParam String cardNumber, @RequestParam String password, @RequestParam Double amount) {
        if (atmService.depositMoney(id, cardNumber, password, amount)) {
            return ResponseEntity.ok("Money deposited successfully");
        }
        return ResponseEntity.badRequest().body("Money deposit failed");
    } // http://localhost:8080/atm/user/depositMoney?id=...&bankCardId=...&password=...&amount=...

    @PutMapping("/withdrawMoney")
    public ResponseEntity<String> withdrawMoney(@RequestParam Long id, @RequestParam String cardNumber, @RequestParam String password, @RequestParam Double amount) {
        if (atmService.withdrawMoney(id, cardNumber, password, amount)) {
            return ResponseEntity.ok("Money withdrawn successfully");
        }
        return ResponseEntity.badRequest().body("Money withdrawal failed");
    } // http://localhost:8080/atm/user/withdrawMoney?id=...&bankCardId=...&password=...&amount=...

    @GetMapping("/getBalance")
    public ResponseEntity<Double> getBalance(@RequestParam String cardNumber, @RequestParam String password) {
        return ResponseEntity.ok(atmService.getBalance(cardNumber, password));
    }   // http://localhost:8080/atm/user/getBalance?bankCardId=...&password=...

    @PutMapping("/payDebtOfCreditCardWithBankCard")
    public ResponseEntity<String> payDebtOfCreditCardWithBankCard(@RequestParam String bankCardNumber,
                                                                  @RequestParam String creditCardNumber,
                                                                  @RequestParam String bankCardPassword,
                                                                  @RequestParam double amount) {
        if (atmService.payDebtOfCreditCardWithBankCard(bankCardNumber, creditCardNumber, bankCardPassword, amount)) {
            return ResponseEntity.ok("Debt paid successfully");
        }
        return ResponseEntity.badRequest().body("Debt payment failed");
    } // http://localhost:8080/atm/user/payDebtOfCreditCardWithBankCard?bankCardId=...&creditCardId=...&bankCardPassword=...&amount=...

}
