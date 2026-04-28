package com.parking.dto;

import com.parking.entity.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSlotRequest {
    @NotBlank
    private String slotNumber;

    @NotNull
    private SlotType type;
}
