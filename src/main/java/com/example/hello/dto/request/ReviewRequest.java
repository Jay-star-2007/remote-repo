package com.example.hello.dto.request;

import com.example.hello.domain.VehicleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull
    private VehicleStatus status;

    private String rejectionReason; // Optional
} 