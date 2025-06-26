package com.example.hello.dto.request;

import com.example.hello.domain.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    private VehicleStatus status;

    private String rejectionReason; // Optional
} 