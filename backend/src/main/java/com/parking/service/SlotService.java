package com.parking.service;

import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import com.parking.entity.ParkingSlot;
import com.parking.entity.SlotType;
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
    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(BookingStatus.ACTIVE, BookingStatus.BOOKED);

    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingRepository bookingRepository;

    public List<ParkingSlot> allSlots(SlotType type) {
        if (type == null) {
            return parkingSlotRepository.findByIsActiveTrue();
        }
        return parkingSlotRepository.findByIsActiveTrueAndType(type);
    }

    public List<ParkingSlot> availableSlots(LocalDateTime start, LocalDateTime end) {
        refreshCompletedBookings();

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        List<Booking> overlaps = bookingRepository.findByStatusInAndStartTimeBeforeAndEndTimeAfter(
                BLOCKING_STATUSES,
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

    private void refreshCompletedBookings() {
        List<Booking> legacyBookings = bookingRepository.findByStatus(BookingStatus.BOOKED);
        legacyBookings.forEach(booking -> booking.setStatus(BookingStatus.ACTIVE));
        bookingRepository.saveAll(legacyBookings);

        List<Booking> expired = bookingRepository.findByStatusAndEndTimeBefore(BookingStatus.ACTIVE, LocalDateTime.now());
        expired.forEach(booking -> booking.setStatus(BookingStatus.COMPLETED));
        bookingRepository.saveAll(expired);
    }
}
