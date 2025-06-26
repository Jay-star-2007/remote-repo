package com.example.hello.repository;

import com.example.hello.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByStudentId(String studentId);

    Boolean existsByPhone(String phone);

    Boolean existsByStudentId(String studentId);
} 