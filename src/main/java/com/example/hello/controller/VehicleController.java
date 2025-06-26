package com.example.hello.controller;

import com.example.hello.domain.Vehicle;
import com.example.hello.dto.request.VehicleApplicationRequest;
import com.example.hello.dto.response.MessageResponse;
import com.example.hello.dto.response.VehicleResponse;
import com.example.hello.service.PassTokenService;
import com.example.hello.service.VehicleService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final PassTokenService passTokenService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForVehicle(@Valid @RequestBody VehicleApplicationRequest request) {
        try {
            vehicleService.applyForVehicle(request);
            return ResponseEntity.ok(new MessageResponse("申请已提交，等待审核"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("申请失败: " + e.getMessage()));
        }
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles() {
        return ResponseEntity.ok(vehicleService.getMyVehicles());
    }

    @GetMapping("/pass-qrcode")
    public ResponseEntity<byte[]> getPassQrCode() {
        try {
            // 1. Get current user's approved vehicle
            Vehicle vehicle = vehicleService.findMyApprovedVehicle()
                    .orElseThrow(() -> new RuntimeException("没有找到已批准的车辆或车辆信息不完整"));

            // 2. Generate a short-lived pass token
            String token = passTokenService.generateToken(vehicle.getId());

            // 3. Create the verification URL
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/pass/")
                    .path(token)
                    .build()
                    .toUriString();

            // 4. Generate QR Code in memory
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // 5. Return the image
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(pngData);

        } catch (Exception e) {
            // Log the exception and return an appropriate error response
            // For simplicity, returning 500. In a real app, handle different exceptions differently.
            return ResponseEntity.internalServerError().build();
        }
    }
} 