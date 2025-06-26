package com.example.hello.controller;

import com.example.hello.dto.request.VehicleApplicationRequest;
import com.example.hello.dto.response.MessageResponse;
import com.example.hello.dto.response.VehicleResponse;
import com.example.hello.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> applyForVehiclePermit(@Valid @RequestBody VehicleApplicationRequest vehicleRequest) {
        vehicleService.applyForVehiclePermit(vehicleRequest);
        return ResponseEntity.ok(new MessageResponse("车辆申请已提交，请等待审核。"));
    }

    @GetMapping("/my-vehicle")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserVehicle() {
        return vehicleService.getCurrentUserVehicle()
                .map(vehicle -> {
                    // Generate pass token only if vehicle is approved
                    VehicleResponse response = vehicleService.createVehicleResponse(vehicle);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 