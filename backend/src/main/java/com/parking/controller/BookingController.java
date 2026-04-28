package com.parking.controller;

import com.parking.dto.BookingRequest;
import com.parking.dto.BookingResponse;
import com.parking.entity.BookingStatus;
import com.parking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest request, Principal principal) {
        return bookingService.createBooking(request, principal);
    }

    @GetMapping("/my")
    public List<BookingResponse> myBookings(
            @RequestParam(required = false) BookingStatus status,
            Principal principal
    ) {
        return bookingService.myBookings(status, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String id, Principal principal) {
        bookingService.cancelBooking(id, principal);
    }
}
