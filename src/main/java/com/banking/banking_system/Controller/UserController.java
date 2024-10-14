package com.banking.banking_system.Controller;

import com.banking.banking_system.DTO.UserDTO;
import com.banking.banking_system.Service.MailService;
import com.banking.banking_system.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {
    private final UserService userService;
    private final MailService mailService;

    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok(userService.getUser());
    } // http://localhost:8080/api/user/me?TC=...

    @GetMapping("/bankCard")
    public ResponseEntity<String> doesUserHaveBankCard() {
        if (userService.doesUserHaveBankCard()) {
            return ResponseEntity.ok("User has bank card");
        } else {
            return ResponseEntity.badRequest().body("User does not have bank card");
        }
    } // http://localhost:8080/api/user/bankCard?TC=...

    @GetMapping("/creditCard")
    public ResponseEntity<String> doesUserHaveCreditCard() {
        if (userService.doesUserHaveCreditCard()) {
            return ResponseEntity.ok("User has credit card");
        } else {
            return ResponseEntity.badRequest().body("User does not have credit card");
        }
    } // http://localhost:8080/api/user/creditCard?TC=...

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestParam int age) {
        if (userService.updateUserAge(age)) {
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.badRequest().body("User could not be updated");
        }
    } // http://localhost:8080/api/user/update?TC=...&age=...

    @PostMapping("/reset-account-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam String TC) {
        return ResponseEntity.ok(mailService.resetPasswordRequest(TC));
    } // http://localhost:8080/api/user/reset-account-password-request?TC=...

    @PostMapping("/reset-account-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        return ResponseEntity.ok(mailService.resetPassword(token, password));
    } // http://localhost:8080/api/user/reset-account-password?token=...&password=...


    @PostMapping("/reset-card-password")
    public ResponseEntity<String> resetCardPassword(@RequestParam String token, @RequestParam String password) {
        return ResponseEntity.ok(mailService.resetCardPassword(token, password));
    } // http://localhost:8080/api/user/reset-card-password?token=...&password=...
}
