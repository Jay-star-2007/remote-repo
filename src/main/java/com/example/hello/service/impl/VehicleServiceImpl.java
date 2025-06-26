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
import com.example.hello.service.PassTokenService;
import com.example.hello.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final PassTokenService passTokenService;

    @Override
    @Transactional
    public void applyForVehiclePermit(VehicleApplicationRequest vehicleRequest) {
        String username = SecurityUtils.getCurrentUserUsername()
                .orElseThrow(() -> new IllegalStateException("用户未登录，无法申请"));

        User currentUser = userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到当前用户: " + username));

        // Check if user already has a vehicle
        vehicleRepository.findByOwnerId(currentUser.getId()).ifPresent(v -> {
            throw new IllegalStateException("用户已绑定车辆，无法重复申请");
        });

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(vehicleRequest.getLicensePlate())
                .photoUrl(vehicleRequest.getPhotoUrl())
                .status(VehicleStatus.PENDING_APPROVAL) // Default status
                .owner(currentUser)
                .build();

        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> getCurrentUserVehicle() {
        return SecurityUtils.getCurrentUserUsername()
                .flatMap(userRepository::findByPhone)
                .flatMap(user -> vehicleRepository.findByOwnerId(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getPendingApplications() {
        return vehicleRepository.findAllByStatus(VehicleStatus.PENDING_APPROVAL);
    }

    @Override
    @Transactional
    public Vehicle reviewApplication(Long vehicleId, ReviewRequest reviewRequest) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("未找到ID为 " + vehicleId + " 的车辆"));

        if (vehicle.getStatus() != VehicleStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("该车辆申请已处理，当前状态为: " + vehicle.getStatus());
        }

        // We only allow changing status to APPROVED or REJECTED
        if (reviewRequest.getStatus() != VehicleStatus.APPROVED && reviewRequest.getStatus() != VehicleStatus.REJECTED) {
            throw new IllegalArgumentException("无效的审核状态: " + reviewRequest.getStatus());
        }

        vehicle.setStatus(reviewRequest.getStatus());
        
        if (reviewRequest.getStatus() == VehicleStatus.REJECTED) {
            vehicle.setRejectionReason(reviewRequest.getRejectionReason());
        } else {
            vehicle.setRejectionReason(null); // Clear reason if approved
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    public VehicleResponse createVehicleResponse(Vehicle vehicle) {
        String token = null;
        if (vehicle.getStatus() == VehicleStatus.APPROVED) {
            token = passTokenService.generateToken(vehicle.getId());
        }
        return VehicleResponse.from(vehicle, token);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }
} 