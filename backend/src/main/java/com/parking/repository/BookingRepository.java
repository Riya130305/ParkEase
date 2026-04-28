package com.parking.repository;

import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserIdOrderByStartTimeDesc(String userId);

    List<Booking> findBySlotIdAndStatusAndStartTimeBeforeAndEndTimeAfter(
            String slotId,
            BookingStatus status,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );

    List<Booking> findByStatusAndStartTimeBeforeAndEndTimeAfter(
            BookingStatus status,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );
}
