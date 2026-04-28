package com.parking.config;

import com.parking.entity.ParkingSlot;
import com.parking.entity.SlotType;
import com.parking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    public void run(String... args) {
        if (parkingSlotRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= 20; i++) {
            parkingSlotRepository.save(ParkingSlot.builder()
                    .slotNumber("A" + i)
                    .type(i <= 14 ? SlotType.CAR : SlotType.BIKE)
                    .isActive(true)
                    .build());
        }
    }
}
