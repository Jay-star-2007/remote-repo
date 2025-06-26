package com.example.hello.service;

public interface PassTokenService {
    String generateToken(Long vehicleId);
    boolean validateToken(String token);
    Long getVehicleIdFromToken(String token);
} 