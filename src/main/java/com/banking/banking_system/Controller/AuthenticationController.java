package com.banking.banking_system.Controller;

import com.banking.banking_system.Authentication.AuthenticationRequest;
import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterEmployeeRequest;
import com.banking.banking_system.Authentication.RegisterUserRequest;
import com.banking.banking_system.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registerUser")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(authenticationService.registerUser(registerUserRequest));
    } // http://localhost:8080/api/auth/registerUser

    @PostMapping("/createAdmin")
    public ResponseEntity<AuthenticationResponse> createAdmin(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(authenticationService.createAdmin(username, password));
    } // http://localhost:8080/api/auth/createAdmin?username=...&password=...

    @PostMapping("/authenticateUser")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticateUser(authenticationRequest));
    } // http://localhost:8080/api/auth/authenticateUser

    @PostMapping("/authenticateEmployee")
    public ResponseEntity<AuthenticationResponse> authenticateEmployee(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticateEmployee(authenticationRequest));
    } // http://localhost:8080/api/auth/authenticateEmployee

    @PostMapping("/authenticateAdmin")
    public ResponseEntity<AuthenticationResponse> authenticateAdmin(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(authenticationService.authenticateAdmin(username, password));
    } // http://localhost:8080/api/auth/authenticateAdmin?username=...&password=...

    @PostMapping("/refreshTokenForUser")
    public ResponseEntity<AuthenticationResponse> refreshTokenForUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authenticationService.refreshTokenForUser(request, response);
    }
}
