package com.example.hello.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String phone;
    private String studentId;
    // We can add roles here later if needed

    public JwtResponse(String accessToken, Long id, String phone, String studentId) {
        this.token = accessToken;
        this.id = id;
        this.phone = phone;
        this.studentId = studentId;
    }
} 