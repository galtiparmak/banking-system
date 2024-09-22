package com.banking.banking_system.Controller;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.Service.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bankCard")
@PreAuthorize("hasRole('USER')")
public class BankCardController {
    private final BankCardService bankCardService;

    @Autowired
    public BankCardController(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    @GetMapping("/getBankCard")
    public ResponseEntity<CardDTO> getBankCard(@RequestParam String cardNumber, @RequestParam String password) {
        CardDTO cardDTO = bankCardService.getBankCard(cardNumber, password);
        return ResponseEntity.ok(cardDTO);
    } // http://localhost:8080/api/bankCard/getBankCard/?cardNumber=...&password=...

    @PutMapping("/spendMoney")
    public ResponseEntity<String> spendMoney(@RequestParam String cardNumber, @RequestParam String password, @RequestParam double amount) {
        if (bankCardService.spendMoney(cardNumber, password, amount)) {
            return ResponseEntity.ok("Money is spent successfully.");
        } else {
            return ResponseEntity.badRequest().body("Money is not spent.");
        }
    } // http://localhost:8080/api/bankCard/spendMoney/?cardNumber=...&password=...&amount=...

    @PutMapping("/moneyTransaction")
    public ResponseEntity<String> moneyTransaction(@RequestParam String cardNumber, @RequestParam String password, @RequestParam String receiverIban, @RequestParam double amount) {
        if (bankCardService.moneyTransaction(cardNumber, password, receiverIban, amount)) {
            return ResponseEntity.ok("Money is transferred successfully.");
        } else {
            return ResponseEntity.badRequest().body("Money is not transferred.");
        }
    } // http://localhost:8080/api/bankCard/moneyTransaction/?cardNumber=...&password=...&receiverIban=...&amount=...

    @PutMapping("/payDebt")
    public ResponseEntity<String> payDebt(@RequestParam String cardNumber, @RequestParam String password, @RequestParam String creditCardNumber, @RequestParam double amount) {
        if (bankCardService.payDebt(cardNumber, password, creditCardNumber, amount)) {
            return ResponseEntity.ok("Debt is paid successfully.");
        } else {
            return ResponseEntity.badRequest().body("Debt is not paid.");
        }
    } // http://localhost:8080/api/bankCard/payDebt/?cardNumber=...&password=...&creditCardNumber=...&amount=...
}
