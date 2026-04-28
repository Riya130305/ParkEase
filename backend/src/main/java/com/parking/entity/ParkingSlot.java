package com.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_slots")
public class ParkingSlot {
    @Id
    private String id;

    @Indexed(unique = true)
    private String slotNumber;

    private SlotType type;

    @Field("isActive")
    private boolean isActive;
}
