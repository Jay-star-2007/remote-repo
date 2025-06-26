package com.example.hello.service.impl;

import com.example.hello.domain.Role;
import com.example.hello.domain.User;
import com.example.hello.dto.request.LoginRequest;
import com.example.hello.dto.request.RegisterRequest;
import com.example.hello.dto.response.JwtResponse;
import com.example.hello.repository.UserRepository;
import com.example.hello.security.jwt.JwtUtils;
import com.example.hello.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            // In a real application, you should throw a custom exception
            throw new IllegalArgumentException("错误：该手机号已被注册");
        }

        if (userRepository.existsByStudentId(registerRequest.getStudentId())) {
            throw new IllegalArgumentException("错误：该学号/工号已被注册");
        }

        // Create new user's account and assign a default role
        // Here we default to ROLE_STUDENT. This can be extended later to handle different roles.
        User user = User.builder()
                .phone(registerRequest.getPhone())
                .studentId(registerRequest.getStudentId())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(Role.ROLE_STUDENT))
                .build();

        userRepository.save(user);
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        // Step 1: Check if user exists. This provides a better error message.
        userRepository.findByPhone(loginRequest.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("该手机号未注册"));

        // Step 2: If user exists, proceed with authentication.
        // This will throw BadCredentialsException if the password is wrong.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(), // which is phone
                userDetails.getStudentId());
    }
} 