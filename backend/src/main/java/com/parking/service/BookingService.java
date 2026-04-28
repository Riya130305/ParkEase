package com.parking.service;

import com.parking.dto.BookingRequest;
import com.parking.dto.BookingResponse;
import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import com.parking.entity.ParkingSlot;
import com.parking.entity.User;
import com.parking.exception.ConflictException;
import com.parking.exception.NotFoundException;
import com.parking.repository.BookingRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private static final BigDecimal HOURLY_RATE = BigDecimal.valueOf(50);
    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(BookingStatus.ACTIVE, BookingStatus.BOOKED);

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final UserRepository userRepository;

    public synchronized BookingResponse createBooking(BookingRequest request, Principal principal) {
        refreshCompletedBookings();

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        User user = currentUser(principal);
        ParkingSlot slot = parkingSlotRepository.findById(request.getSlotId())
                .filter(ParkingSlot::isActive)
                .orElseThrow(() -> new NotFoundException("Parking slot not found"));

        boolean overlapExists = !bookingRepository.findBySlotIdAndStatusInAndStartTimeBeforeAndEndTimeAfter(
                slot.getId(),
                BLOCKING_STATUSES,
                request.getEndTime(),
                request.getStartTime()
        ).isEmpty();

        if (overlapExists) {
            throw new ConflictException("Slot already booked for this time range");
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .userId(user.getId())
                .slotId(slot.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getEndTime().isBefore(LocalDateTime.now()) ? BookingStatus.COMPLETED : BookingStatus.ACTIVE)
                .cost(calculateCost(request))
                .build());

        return toResponse(booking, slot);
    }

    public List<BookingResponse> myBookings(BookingStatus status, Principal principal) {
        refreshCompletedBookings();

        User user = currentUser(principal);
        List<Booking> bookings;
        if (status == null) {
            bookings = bookingRepository.findByUserIdOrderByStartTimeDesc(user.getId());
        } else if (status == BookingStatus.ACTIVE) {
            bookings = bookingRepository.findByUserIdAndStatusInOrderByStartTimeDesc(user.getId(), BLOCKING_STATUSES);
        } else {
            bookings = bookingRepository.findByUserIdAndStatusOrderByStartTimeDesc(user.getId(), status);
        }

        return bookings.stream()
                .map(booking -> toResponse(booking, findSlot(booking.getSlotId())))
                .toList();
    }

    public void cancelBooking(String bookingId, Principal principal) {
        User user = currentUser(principal);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getUserId().equals(user.getId())) {
            throw new NotFoundException("Booking not found");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public void refreshCompletedBookings() {
        normalizeLegacyBookedStatus();

        List<Booking> expired = bookingRepository.findByStatusAndEndTimeBefore(BookingStatus.ACTIVE, LocalDateTime.now());
        expired.forEach(booking -> booking.setStatus(BookingStatus.COMPLETED));
        bookingRepository.saveAll(expired);
    }

    private void normalizeLegacyBookedStatus() {
        List<Booking> legacyBookings = bookingRepository.findByStatus(BookingStatus.BOOKED);
        legacyBookings.forEach(booking -> booking.setStatus(BookingStatus.ACTIVE));
        bookingRepository.saveAll(legacyBookings);
    }

    private BigDecimal calculateCost(BookingRequest request) {
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return HOURLY_RATE.multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }

    private User currentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private ParkingSlot findSlot(String slotId) {
        return parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Parking slot not found"));
    }

    private BookingResponse toResponse(Booking booking, ParkingSlot slot) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .slotId(booking.getSlotId())
                .slotNumber(slot.getSlotNumber())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus() == BookingStatus.BOOKED ? BookingStatus.ACTIVE : booking.getStatus())
                .cost(booking.getCost())
                .build();
    }
}
