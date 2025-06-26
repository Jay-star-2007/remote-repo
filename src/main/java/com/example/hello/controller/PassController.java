package com.example.hello.controller;

import com.example.hello.domain.Vehicle;
import com.example.hello.service.PassTokenService;
import com.example.hello.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pass")
@RequiredArgsConstructor
public class PassController {

    private final PassTokenService passTokenService;
    private final VehicleService vehicleService; // Assuming VehicleService can fetch a vehicle by ID

    @GetMapping("/{token}")
    public String getPassDetails(@PathVariable String token, Model model) {
        try {
            if (!passTokenService.validateToken(token)) {
                model.addAttribute("error", "无效或已过期的通行码");
                return "pass-error"; // An error view
            }

            Long vehicleId = passTokenService.getVehicleIdFromToken(token);
            // We need a method in VehicleService to get vehicle by ID
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("车辆不存在"));

            model.addAttribute("vehicle", vehicle);
            model.addAttribute("owner", vehicle.getOwner());

            return "pass-details"; // The success view
        } catch (Exception e) {
            model.addAttribute("error", "验证时发生错误：" + e.getMessage());
            return "pass-error";
        }
    }
} 