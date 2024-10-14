package com.banking.banking_system.Configuration;

import com.banking.banking_system.Entity.SessionToken;
import com.banking.banking_system.Repository.SessionTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final SessionTokenRepository sessionTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final String jwt = authHeader.substring(7);

        SessionToken token = sessionTokenRepository.findByToken(jwt).orElse(null);

        if (token != null) {
            token.setLoggedOut(true);
            sessionTokenRepository.save(token);
        }
    }
}
