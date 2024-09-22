package com.banking.banking_system.Service;

import com.banking.banking_system.Authentication.AuthenticationRequest;
import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterUserRequest;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import com.banking.banking_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerUser(RegisterUserRequest request) {
        System.out.println("TC Value: " + request.getTc());
        var _user = User
                .builder()
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .role(Role.USER)
                .createdAt(new Date())
                .creditCards(new ArrayList<>())
                .bankCards(new ArrayList<>())
                .build();

        userRepository.save(_user);

        var token = jwtService.generateToken(_user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse createAdmin(String username, String password) {
        var admin = Admin
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();

        adminRepository.save(admin);

        var token = jwtService.generateToken(admin);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticateAdmin(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        );

        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        var token = jwtService.generateToken(admin);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTc(), request.getPassword()
                )
        );

        var user = userRepository.findByTc(request.getTc())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var token = jwtService.generateToken(user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticateEmployee(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTc(), request.getPassword()
                )
        );

        var employee = employeeRepository.findByTc(request.getTc())
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        var token = jwtService.generateToken(employee);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }
}
