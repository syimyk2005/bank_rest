package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "card owner id can not be empty")
    private Long user;

    @Future(message = "expiration date must be in the future")
    private LocalDate expirationDate;

    @NotNull(message = "card status is required")
    private CardStatus status;

    @Min(value = 0, message = "balance cannot be negative")
    private Double balance = 0.0;

}

