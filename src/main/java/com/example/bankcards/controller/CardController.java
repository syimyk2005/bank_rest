package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ChangeCardStatusDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CardRequestDto cardRequestDto) {
        CardResponseDto createdCard = cardService.createCard(cardRequestDto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @PatchMapping("/status")
    public ResponseEntity<CardResponseDto> changeCardStatus(@Valid @RequestBody ChangeCardStatusDto statusDto) {
        CardResponseDto updatedCard = cardService.changeCardStatus(statusDto);
        return ResponseEntity.ok(updatedCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> findCard(@PathVariable Long id) {
        CardResponseDto card = cardService.findCard(id);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        String message = cardService.deleteCard(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        List<CardResponseDto> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(cardService.getCardsForCurrentUser(search, page, size));
    }

}
