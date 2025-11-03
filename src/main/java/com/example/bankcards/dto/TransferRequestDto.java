package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransferRequestDto {

    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    private String fromCardNumber;

    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    private String toCardNumber;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}

