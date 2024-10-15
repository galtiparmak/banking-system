package com.banking.banking_system.Service;

import com.banking.banking_system.Authentication.AuthenticationRequest;
import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterUserRequest;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import com.banking.banking_system.Repository.SessionTokenRepository;
import com.banking.banking_system.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SessionTokenRepository sessionTokenRepository;

    public AuthenticationResponse registerUser(RegisterUserRequest request) {
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

        var accessToken = jwtService.generateAccessToken(_user);
        var refreshToken = jwtService.generateRefreshToken(_user);

        saveSessionToken(_user, accessToken, refreshToken);

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        var accessToken = jwtService.generateAccessToken(admin);
        var refreshToken = jwtService.generateRefreshToken(admin);

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        var accessToken = jwtService.generateAccessToken(admin);
        var refreshToken = jwtService.generateRefreshToken(admin);

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokenByUser(user);
        saveSessionToken(user, accessToken, refreshToken);

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        var accessToken = jwtService.generateAccessToken(employee);
        var refreshToken = jwtService.generateRefreshToken(employee);

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public ResponseEntity refreshTokenForUser(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String tc = jwtService.extractUserName(token);
        User user = userRepository.findByTc(tc)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));

        if (jwtService.isRefreshTokenValid(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveSessionToken(user, accessToken, refreshToken);

            return new ResponseEntity(AuthenticationResponse
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    private void saveSessionToken(User user, String accessToken, String refreshToken) {
        SessionToken sessionToken = new SessionToken();
        sessionToken.setUser(user);
        sessionToken.setAccessToken(accessToken);
        sessionToken.setRefreshToken(refreshToken);
        sessionToken.setLoggedOut(false);
        sessionTokenRepository.save(sessionToken);
    }

    private void revokeAllTokenByUser(User user) {
        List<SessionToken> validTokens = sessionTokenRepository.findAllByUser(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(t -> {
                t.setLoggedOut(true);}
            );
        }
        sessionTokenRepository.saveAll(validTokens);
    }
}
