package com.example.hello.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "手机号不能为空")
    @Size(min = 11, max = 11, message = "手机号必须为11位")
    private String phone;

    @NotBlank(message = "学号/工号不能为空")
    private String studentId;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
} 