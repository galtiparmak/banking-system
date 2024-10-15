package com.banking.banking_system.Service;

import com.banking.banking_system.Entity.User;
import com.banking.banking_system.Repository.SessionTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${access.token.expiration}")
    private long accessTokenExpire;

    @Value("${refresh.token.expiration}")
    private long refreshTokenExpire;

    private final SessionTokenRepository sessionTokenRepository;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> claims, UserDetails userDetails) {
        return generateToken(claims, userDetails, accessTokenExpire);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return generateToken(claims, userDetails, refreshTokenExpire);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails, long expiryTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        boolean isValidToken = sessionTokenRepository.findByAccessToken(token)
                                .map(t -> !t.isLoggedOut()).orElse(false);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isValidToken;
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        boolean isValidToken = sessionTokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isValidToken;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
}
