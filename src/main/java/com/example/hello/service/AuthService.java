package com.example.hello.service;

import com.example.hello.dto.request.LoginRequest;
import com.example.hello.dto.request.RegisterRequest;
import com.example.hello.dto.response.JwtResponse;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    JwtResponse login(LoginRequest loginRequest);
} 