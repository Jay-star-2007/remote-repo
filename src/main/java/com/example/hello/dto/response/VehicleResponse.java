package com.example.hello.dto.response;

import com.example.hello.domain.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String licensePlate;
    private String photoUrl;
    private VehicleStatus status;
    private Long ownerId;
    private String ownerPhone;
    private String passToken;
    private String rejectionReason;
} 