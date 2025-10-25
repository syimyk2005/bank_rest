package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardResponseDto {

    private Long id;
    private String cardNumber;
    private Long user;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;

}
