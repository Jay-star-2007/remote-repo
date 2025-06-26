package com.example.hello.repository;

import com.example.hello.domain.Vehicle;
import com.example.hello.domain.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByOwnerId(Long ownerId);

    List<Vehicle> findAllByStatus(VehicleStatus status);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findAllByOwnerId(Long ownerId);

    Optional<Vehicle> findByOwnerIdAndStatus(Long ownerId, VehicleStatus status);
} 