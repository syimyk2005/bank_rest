package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardBlockingRequest {

    @NotNull(message = "cardNumber can't be empty")
    private Long cardId;
    private String comment;

}
