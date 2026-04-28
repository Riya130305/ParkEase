package com.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    private String userId;
    private String slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private BigDecimal cost;
}
