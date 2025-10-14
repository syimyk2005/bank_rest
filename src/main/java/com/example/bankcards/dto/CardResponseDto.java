package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardResponseDto {

    private Long id;
    private String cardNumber;
    private String owner;
    private LocalDate expirationDate;
    private CardStatus status;
    private Long balance;

}
