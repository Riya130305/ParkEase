package com.parking.service;

import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import com.parking.entity.ParkingSlot;
import com.parking.repository.BookingRepository;
import com.parking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingRepository bookingRepository;

    public List<ParkingSlot> allSlots() {
        return parkingSlotRepository.findByIsActiveTrue();
    }

    public List<ParkingSlot> availableSlots(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        List<Booking> overlaps = bookingRepository.findByStatusAndStartTimeBeforeAndEndTimeAfter(
                BookingStatus.BOOKED,
                end,
                start
        );
        Set<String> bookedSlotIds = overlaps.stream()
                .map(Booking::getSlotId)
                .collect(Collectors.toSet());

        return parkingSlotRepository.findByIsActiveTrue().stream()
                .filter(slot -> !bookedSlotIds.contains(slot.getId()))
                .toList();
    }
}
