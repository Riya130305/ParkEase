package com.parking.config;

import com.parking.entity.ParkingSlot;
import com.parking.entity.SlotType;
import com.parking.entity.User;
import com.parking.entity.UserRole;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final ParkingSlotRepository parkingSlotRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        mongoTemplate.updateMulti(
                Query.query(where("status").is("BOOKED")),
                Update.update("status", "ACTIVE"),
                "bookings"
        );

        userRepository.findByEmail("admin@parking.com").ifPresentOrElse(admin -> {
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }, () -> {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@parking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .build());
        });

        if (parkingSlotRepository.count() == 0) {
            for (int i = 1; i <= 20; i++) {
                parkingSlotRepository.save(ParkingSlot.builder()
                        .slotNumber("A" + i)
                        .type(i <= 14 ? SlotType.CAR : SlotType.BIKE)
                        .isActive(true)
                        .build());
            }
        }
    }
}
