package com.example.hello.service;

import com.example.hello.domain.Vehicle;
import com.example.hello.dto.request.ReviewRequest;
import com.example.hello.dto.request.VehicleApplicationRequest;
import com.example.hello.dto.response.VehicleResponse;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    void applyForVehiclePermit(VehicleApplicationRequest vehicleRequest);

    Optional<Vehicle> getCurrentUserVehicle();

    VehicleResponse createVehicleResponse(Vehicle vehicle);

    List<Vehicle> getPendingApplications();

    Vehicle reviewApplication(Long vehicleId, ReviewRequest reviewRequest);

    Optional<Vehicle> getVehicleById(Long vehicleId);
} 