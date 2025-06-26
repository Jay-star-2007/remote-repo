package com.example.hello.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleApplicationRequest {

    @NotBlank(message = "车牌号不能为空")
    private String licensePlate;

    @NotBlank(message = "车辆照片URL不能为空")
    private String photoUrl;
} 