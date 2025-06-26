package com.example.hello.controller;

import com.example.hello.dto.request.ReviewRequest;
import com.example.hello.dto.response.VehicleResponse;
import com.example.hello.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VehicleService vehicleService;

    @GetMapping("/vehicles/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getPendingApplications() {
        List<VehicleResponse> pendingVehicles = vehicleService.getPendingApplications().stream()
                .map(vehicleService::createVehicleResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingVehicles);
    }

    @PostMapping("/vehicles/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> reviewApplication(@PathVariable Long id, @Valid @RequestBody ReviewRequest reviewRequest) {
        VehicleResponse updatedVehicle = vehicleService.createVehicleResponse(
                vehicleService.reviewApplication(id, reviewRequest)
        );
        return ResponseEntity.ok(updatedVehicle);
    }
} 