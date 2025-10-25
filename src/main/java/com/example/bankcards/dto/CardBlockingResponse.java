package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardBlockingResponse {
    private Long id;

    private String cardNumber;

    private String comment;
}
