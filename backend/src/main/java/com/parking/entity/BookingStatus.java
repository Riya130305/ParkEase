package com.parking.entity;

public enum BookingStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,

    // Legacy value from the first MVP version. Kept so old Mongo records can be read and migrated.
    BOOKED
}
