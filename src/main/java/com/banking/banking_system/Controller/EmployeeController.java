package com.banking.banking_system.Controller;

import com.banking.banking_system.DTO.CardDTO;
import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.DTO.UserDTO;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Service.EmployeeService;
import com.banking.banking_system.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final MailService mailService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, MailService mailService) {
        this.employeeService = employeeService;
        this.mailService = mailService;
    }

    @GetMapping("/employee")
    public ResponseEntity<EmployeeDTO> getEmployee(@RequestParam String TC) {
        return ResponseEntity.ok(employeeService.getEmployee(TC));
    } // http://localhost:8080/api/employee/employee?TC=...

    @PutMapping("/update-age")
    public ResponseEntity<Void> updateEmployeeAge(@RequestParam String TC, @RequestParam int age) {
        employeeService.updateEmployeeAge(TC, age);
        return ResponseEntity.ok().build();
    } // http://localhost:8080/api/employee/update-age?TC=...&age=...

    @PutMapping("/update-position")
    public ResponseEntity<Void> updateEmployeePosition(@RequestParam String TC, @RequestParam String position) {
        employeeService.updateEmployeePosition(TC, position);
        return ResponseEntity.ok().build();
    } // http://localhost:8080/api/employee/update-position?TC=...&position=...

    @PutMapping("/update-phone-number")
    public ResponseEntity<Void> updateEmployeePhoneNumber(@RequestParam String TC, @RequestParam String phoneNumber) {
        employeeService.updateEmployeePhoneNumber(TC, phoneNumber);
        return ResponseEntity.ok().build();
    } // http://localhost:8080/api/employee/update-phone-number?TC=...&phoneNumber=...

    @PutMapping("/update-department")
    public ResponseEntity<Void> updateEmployeeDepartment(@RequestParam String TC, @RequestParam String department) {
        employeeService.updateEmployeeDepartment(TC, department);
        return ResponseEntity.ok().build();
    } // http://localhost:8080/api/employee/update-department?TC=...&department=...

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(employeeService.getAllUsers());
    } // http://localhost:8080/api/employee/users

    @GetMapping("/credit-cards")
    public ResponseEntity<List<CardDTO>> getAllCreditCards() {
        return ResponseEntity.ok(employeeService.getAllCreditCards());
    } // http://localhost:8080/api/employee/credit-cards

    @GetMapping("/bank-cards")
    public ResponseEntity<List<CardDTO>> getAllBankCards() {
        return ResponseEntity.ok(employeeService.getAllBankCards());
    } // http://localhost:8080/api/employee/bank-cards

    @GetMapping("/cards-of-user")
    public ResponseEntity<List<CardDTO>> getAllCardsOfUser(@RequestParam String TC) {
        return ResponseEntity.ok(employeeService.getAllCardsOfUser(TC));
    } // http://localhost:8080/api/employee/cards-of-user?TC=...

    @GetMapping("/card-requests")
    public ResponseEntity<List<CardRequest>> getAllCardRequests() {
        return ResponseEntity.ok(employeeService.getAllCardRequests());
    } // http://localhost:8080/api/employee/card-requests

    @GetMapping("/card-requests/{id}")
    public ResponseEntity<CardRequest> getCardRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getCardRequestById(id));
    } // http://localhost:8080/api/employee/card-requests/...

    @PostMapping("/approve-credit-card-request")
    public ResponseEntity<String> approveCreditCardRequest(@RequestParam Long id, @RequestParam double amount) {
        if (employeeService.approveCreditCardRequest(id, amount)) {
            return ResponseEntity.ok("Credit card request approved");
        }
        return ResponseEntity.badRequest().body("Credit card request not approved");
    } // http://localhost:8080/api/employee/approve-credit-card-request?id=...&amount=...

    @PostMapping("/approve-bank-card-request")
    public ResponseEntity<String> approveBankCardRequest(@RequestParam Long id) {
        if (employeeService.approveBankCardRequest(id)) {
            return ResponseEntity.ok("Bank card request approved");
        }
        return ResponseEntity.badRequest().body("Bank card request not approved");
    } // http://localhost:8080/api/employee/approve-bank-card-request?id=...

    @PostMapping("/reset-card-password-request")
    public ResponseEntity<String> cardPasswordResetRequest(@RequestParam String cardNumber) {
        if (mailService.resetCardPasswordRequest(cardNumber)) {
            return ResponseEntity.ok("Card password reset request email sent");
        }
        return ResponseEntity.badRequest().body("Card password reset request email not sent");
    } // http://localhost:8080/api/employee/reset-card-password-request?cardNumber=...

    @GetMapping("/get-card-password-reset-tokens")
    public ResponseEntity<List<CardPasswordResetToken>> getCardPasswordResetTokens() {
        return ResponseEntity.ok(employeeService.getAllCardPasswordResetTokens());
    } // http://localhost:8080/api/employee/get-card-password-reset-tokens

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam String TC) {
        if (employeeService.deleteUser(TC)) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("User could not be deleted");
        }
    } // http://localhost:8080/api/employee/delete-user?TC=...

}
