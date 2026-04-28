package com.parking.dto;

import com.parking.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String id;
    private String userId;
    private String slotId;
    private String slotNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private BigDecimal cost;
}
