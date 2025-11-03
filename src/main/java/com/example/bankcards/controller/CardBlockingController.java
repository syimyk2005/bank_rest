package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.service.CardBlockingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST-контроллер, отвечающий за операции блокировки банковских карт.
 * <p>
 * Содержит эндпоинты для запроса и подтверждения блокировки карты.
 */
@RestController
@RequestMapping("/api/cards/blocking")
@RequiredArgsConstructor
public class CardBlockingController {


    private final CardBlockingService cardBlockingService;

    /**
     * Отправляет запрос на блокировку карты.
     *
     * @param request объект {@link CardBlockingRequest}, содержащий номер карты и причину блокировки
     * @return сообщение о результате операции
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestBlocking(@RequestBody @Valid CardBlockingRequest request) {
        String message = cardBlockingService.requestCardBlocking(request);
        return ResponseEntity.ok(message);
    }


    /**
     * Подтверждает блокировку карты по её номеру.
     *
     * @param cardNumber номер карты, которую требуется заблокировать
     * @return собщение о результате операции
     */
    @PostMapping("/approve/{cardNumber}")
    public ResponseEntity<String> approveBlocking(@PathVariable String cardNumber) {
        String message = cardBlockingService.approveBlocking(cardNumber);
        return ResponseEntity.ok(message);
    }
}
