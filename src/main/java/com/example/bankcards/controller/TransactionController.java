package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер, отвечающий за операции переводов между картами.
 * <p>
 * Содержит эндпоинт для выполнения переводов.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Выполняет перевод средств с одной карты на другую.
     *
     * @param dto объект {@link TransferRequestDto}, содержащий данные перевода:
     *            номер карты отправителя, номер карты получателя, сумму перевода и описание
     * @return сообщение о результате операции перевода
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDto dto) {
        return ResponseEntity.ok(transactionService.transfer(dto));
    }
}
