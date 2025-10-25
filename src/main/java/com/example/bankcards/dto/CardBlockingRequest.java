package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Запрос на блокировку банковской карты.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardBlockingRequest {

    /** Номер карты, которую нужно заблокировать. */
    @NotBlank(message = "cardNumber can't be empty")
    private String cardNumber;

    /** Комментарий к запросу. */
    @NotBlank(message = "comment can't be empty")
    private String comment;
}

