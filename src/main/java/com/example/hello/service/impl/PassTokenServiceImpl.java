package com.example.hello.service.impl;

import com.example.hello.service.PassTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class PassTokenServiceImpl implements PassTokenService {

    private static final Logger logger = LoggerFactory.getLogger(PassTokenServiceImpl.class);

    // We'll use a separate, dedicated secret for pass tokens.
    // This should be a long, random string.
    @Value("${app.passTokenSecret}")
    private String passTokenSecret;

    @Value("${app.passTokenExpirationMs}")
    private int passTokenExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(passTokenSecret.getBytes());
    }

    @Override
    public String generateToken(Long vehicleId) {
        return Jwts.builder()
                .setSubject(Long.toString(vehicleId))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + passTokenExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Long getVehicleIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
} 