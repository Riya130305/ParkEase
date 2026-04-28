package com.parking.service;

import com.parking.dto.CreateSlotRequest;
import com.parking.dto.RevenueResponse;
import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import com.parking.entity.ParkingSlot;
import com.parking.exception.ConflictException;
import com.parking.exception.NotFoundException;
import com.parking.repository.BookingRepository;
import com.parking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public ParkingSlot createSlot(CreateSlotRequest request) {
        if (parkingSlotRepository.existsBySlotNumber(request.getSlotNumber())) {
            throw new ConflictException("Slot number already exists");
        }

        return parkingSlotRepository.save(ParkingSlot.builder()
                .slotNumber(request.getSlotNumber())
                .type(request.getType())
                .isActive(true)
                .build());
    }

    public void deleteSlot(String id) {
        ParkingSlot slot = parkingSlotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parking slot not found"));
        slot.setActive(false);
        parkingSlotRepository.save(slot);
    }

    public RevenueResponse revenue() {
        bookingService.refreshCompletedBookings();

        BigDecimal totalRevenue = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .filter(booking -> booking.getCost() != null)
                .map(Booking::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueResponse(
                bookingRepository.count(),
                bookingRepository.countByStatus(BookingStatus.ACTIVE),
                bookingRepository.countByStatus(BookingStatus.COMPLETED),
                bookingRepository.countByStatus(BookingStatus.CANCELLED),
                totalRevenue
        );
    }
}
