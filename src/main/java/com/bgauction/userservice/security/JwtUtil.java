package com.bgauction.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String keyString) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
    }

    public String generateToken(Long userId, String email, String role) {
        long expirationTime = 86400000; // 1 day
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
}
