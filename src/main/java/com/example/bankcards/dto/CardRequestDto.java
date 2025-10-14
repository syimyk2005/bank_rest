package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardRequestDto {

    @Pattern(regexp = "\\d{16}", message = "Card number must contain exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "card owner name cannot be empty")
    private String owner;

    @Future(message = "expiration date must be in the future")
    private LocalDate expirationDate;

    @NotNull(message = "card status is required")
    private CardStatus status;

    @Min(value = 0, message = "balance cannot be negative")
    private Long balance = 0L;
}

