package com.parking.controller;

import com.parking.entity.ParkingSlot;
import com.parking.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;

    @GetMapping
    public List<ParkingSlot> allSlots() {
        return slotService.allSlots();
    }

    @GetMapping("/available")
    public List<ParkingSlot> availableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return slotService.availableSlots(start, end);
    }
}
