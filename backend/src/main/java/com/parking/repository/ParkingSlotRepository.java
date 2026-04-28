package com.parking.repository;

import com.parking.entity.ParkingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, String> {
    @Query("{ 'isActive': true }")
    List<ParkingSlot> findByIsActiveTrue();

    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
}
