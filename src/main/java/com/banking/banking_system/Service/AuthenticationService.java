package com.banking.banking_system.Service;

import com.banking.banking_system.Authentication.AuthenticationRequest;
import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterEmployeeRequest;
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

    public AuthenticationResponse register(RegisterUserRequest request) {
        var _user = User
                .builder()
                .TC(request.getTC())
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

    public AuthenticationResponse registerEmployee(RegisterEmployeeRequest request) {
        var employee = Employee
                .builder()
                .TC(request.getTC())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .role(Role.EMPLOYEE)
                .position(request.getPosition())
                .department(request.getDepartment())
                .phoneNumber(request.getPhoneNumber())
                .createdAt(new java.util.Date())
                .build();

        employeeRepository.save(employee);

        var token = jwtService.generateToken(employee);

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
                .build();

        adminRepository.save(admin);

        var token = jwtService.generateToken(admin);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTC(), request.getPassword()
                )
        );

        var bank_user = userRepository.findByTC(request.getTC())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var token = jwtService.generateToken(bank_user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }
}
