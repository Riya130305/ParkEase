package com.parking.controller;

import com.parking.dto.CreateSlotRequest;
import com.parking.dto.RevenueResponse;
import com.parking.entity.ParkingSlot;
import com.parking.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSlot createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return adminService.createSlot(request);
    }

    @DeleteMapping("/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable String id) {
        adminService.deleteSlot(id);
    }

    @GetMapping("/revenue")
    public RevenueResponse revenue() {
        return adminService.revenue();
    }
}
