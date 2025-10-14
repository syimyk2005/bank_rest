package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.service.CardBlockingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/blocking")
@RequiredArgsConstructor
public class CardBlockingController {

    private final CardBlockingService cardBlockingService;

    @PostMapping("/request")
    public ResponseEntity<String> requestBlocking(@RequestBody @Valid CardBlockingRequest request) {
        String message = cardBlockingService.requestCardBlocking(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/approve/{cardNumber}")
    public ResponseEntity<String> approveBlocking(@PathVariable String cardNumber) {
        String message = cardBlockingService.approveBlocking(cardNumber);
        return ResponseEntity.ok(message);
    }
}
