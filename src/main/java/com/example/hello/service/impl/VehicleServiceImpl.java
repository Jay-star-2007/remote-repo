package com.example.hello.service.impl;

import com.example.hello.domain.User;
import com.example.hello.domain.Vehicle;
import com.example.hello.domain.VehicleStatus;
import com.example.hello.dto.request.ReviewRequest;
import com.example.hello.dto.request.VehicleApplicationRequest;
import com.example.hello.dto.response.VehicleResponse;
import com.example.hello.repository.UserRepository;
import com.example.hello.repository.VehicleRepository;
import com.example.hello.security.SecurityUtils;
import com.example.hello.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Vehicle applyForVehicle(VehicleApplicationRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("无法获取当前用户信息"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("找不到当前用户"));

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .photoUrl(request.getPhotoUrl())
                .status(VehicleStatus.PENDING_APPROVAL)
                .owner(currentUser)
                .build();
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllPendingVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.PENDING_APPROVAL).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Vehicle reviewVehicle(Long id, ReviewRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到ID为 " + id + " 的车辆"));

        if (vehicle.getStatus() != VehicleStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("该车辆申请已处理，当前状态为: " + vehicle.getStatus());
        }

        if (request.getStatus() != VehicleStatus.APPROVED && request.getStatus() != VehicleStatus.REJECTED) {
            throw new IllegalArgumentException("无效的审核状态: " + request.getStatus());
        }

        vehicle.setStatus(request.getStatus());

        if (request.getStatus() == VehicleStatus.REJECTED) {
            vehicle.setRejectionReason(request.getRejectionReason());
        } else {
            vehicle.setRejectionReason(null);
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getMyVehicles() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("无法获取当前用户信息"));

        return vehicleRepository.findAllByOwnerId(currentUserId).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findMyApprovedVehicle() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("无法获取当前用户信息"));
        return vehicleRepository.findByOwnerIdAndStatus(currentUserId, VehicleStatus.APPROVED);
    }

    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        User owner = vehicle.getOwner();
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getPhotoUrl(),
                vehicle.getStatus(),
                owner.getId(),
                owner.getPhone(),
                null,
                vehicle.getRejectionReason()
        );
    }
} 