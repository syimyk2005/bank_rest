package com.example.bankcards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequestDto {
    @NotNull
    private String fromCardNumber;

    @NotNull
    private String toCardNumber;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;
}
