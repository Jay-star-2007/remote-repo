package com.example.hello.controller;

import com.example.hello.dto.request.ReviewRequest;
import com.example.hello.dto.response.MessageResponse;
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
    public List<VehicleResponse> getPendingVehicles() {
        return vehicleService.getAllPendingVehicles();
    }

    @PostMapping("/vehicles/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reviewVehicle(@PathVariable Long id, @Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            vehicleService.reviewVehicle(id, reviewRequest);
            return ResponseEntity.ok(new MessageResponse("审核操作成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("审核失败: " + e.getMessage()));
        }
    }
} 