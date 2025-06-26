package com.example.hello.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class PassTokenService {

    @Value("${app.qr.secret}")
    private String jwtSecret;

    @Value("${app.qr.expiration-ms}")
    private int jwtExpirationMs;

    public String generateToken(Long vehicleId) {
        return Jwts.builder()
                .setSubject(Long.toString(vehicleId))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getVehicleIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (Exception e) {
            // In a real app, you might want to log different exception types
        }
        return false;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
} 