package com.example.hello.dto.response;

import com.example.hello.domain.Vehicle;
import com.example.hello.domain.VehicleStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private Long id;
    private String licensePlate;
    private String photoUrl;
    private VehicleStatus status;
    private Long ownerId;
    private String ownerPhone;
    private String passToken;
    private String rejectionReason;

    public static VehicleResponse from(Vehicle vehicle, String passToken) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .photoUrl(vehicle.getPhotoUrl())
                .status(vehicle.getStatus())
                .rejectionReason(vehicle.getRejectionReason())
                .ownerId(vehicle.getOwner().getId())
                .ownerPhone(vehicle.getOwner().getPhone())
                .passToken(passToken)
                .build();
    }
} 