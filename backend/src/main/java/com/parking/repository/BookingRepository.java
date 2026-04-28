package com.parking.repository;

import com.parking.entity.Booking;
import com.parking.entity.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserIdOrderByStartTimeDesc(String userId);

    List<Booking> findByUserIdAndStatusOrderByStartTimeDesc(String userId, BookingStatus status);

    List<Booking> findByUserIdAndStatusInOrderByStartTimeDesc(String userId, List<BookingStatus> statuses);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByStatusAndEndTimeBefore(BookingStatus status, LocalDateTime now);

    List<Booking> findByStatusInAndEndTimeBefore(List<BookingStatus> statuses, LocalDateTime now);

    List<Booking> findBySlotIdAndStatusInAndStartTimeBeforeAndEndTimeAfter(
            String slotId,
            List<BookingStatus> statuses,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );

    List<Booking> findByStatusInAndStartTimeBeforeAndEndTimeAfter(
            List<BookingStatus> statuses,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );

    long countByStatus(BookingStatus status);
}
