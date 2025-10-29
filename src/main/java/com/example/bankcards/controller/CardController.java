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

/**
 * REST-контроллер, отвечающий за управление банковскими картами.
 * <p>
 * Содержит эндпоинты для создания карты, изменения статуса, поиска, удаления и получения списка карт.
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * Создаёт новую карту.
     *
     * @param cardRequestDto объект {@link CardRequestDto} с данными для создания карты
     * @return объект {@link CardResponseDto} с информацией о созданной карте
     */
    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CardRequestDto cardRequestDto) {
        CardResponseDto createdCard = cardService.createCard(cardRequestDto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    /**
     * Изменяет статус существующей карты.
     *
     * @param statusDto объект {@link ChangeCardStatusDto} с информацией о новом статусе карты
     * @return объект {@link CardResponseDto} с обновлённой информацией о карте
     */
    @PatchMapping("/status")
    public ResponseEntity<CardResponseDto> changeCardStatus(@Valid @RequestBody ChangeCardStatusDto statusDto) {
        CardResponseDto updatedCard = cardService.changeCardStatus(statusDto);
        return ResponseEntity.ok(updatedCard);
    }

    /**
     * Находит карту по её идентификатору.
     *
     * @param id идентификатор карты
     * @return объект {@link CardResponseDto} с информацией о карте
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> findCard(@PathVariable Long id) {
        CardResponseDto card = cardService.findCard(id);
        return ResponseEntity.ok(card);
    }

    /**
     * Удаляет карту по её идентификатору.
     *
     * @param id идентификатор карты
     * @return сообщение об успешном удалении карты
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        String message = cardService.deleteCard(id);
        return ResponseEntity.ok(message);
    }

    /**
     * Получает список всех карт.
     *
     * @return список объектов {@link CardResponseDto} со всеми картами
     */
    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        List<CardResponseDto> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    /**
     * Получает список карт текущего пользователя с поддержкой поиска и пагинации.
     *
     * @param search строка для поиска (опционально)
     * @param page   номер страницы (по умолчанию 0)
     * @param size   размер страницы (по умолчанию 10)
     * @return страница {@link CardResponseDto} с картами текущего пользователя
     */
    @GetMapping("/my")
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(cardService.findCardsForCurrentUser(search, page, size));
    }
}
