package com.example.hello.service;

import com.example.hello.domain.Vehicle;
import com.example.hello.dto.request.ReviewRequest;
import com.example.hello.dto.request.VehicleApplicationRequest;
import com.example.hello.dto.response.VehicleResponse;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    Vehicle applyForVehicle(VehicleApplicationRequest request);

    List<VehicleResponse> getAllPendingVehicles();

    Vehicle reviewVehicle(Long id, ReviewRequest request);

    List<VehicleResponse> getMyVehicles();

    Optional<Vehicle> getVehicleById(Long id);

    Optional<Vehicle> findMyApprovedVehicle();
} 