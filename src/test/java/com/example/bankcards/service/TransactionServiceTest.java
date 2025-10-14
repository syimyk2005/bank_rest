package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setCardNumber("1111222233334444");
        fromCard.setBalance(1000L);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setCardNumber("5555666677778888");
        toCard.setBalance(500L);
    }

    @Test
    void transfer_successful() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(200L);

        when(cardRepository.findByCardNumberForUpdate("1111222233334444"))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumberForUpdate("5555666677778888"))
                .thenReturn(Optional.of(toCard));

        String result = transactionService.transfer(dto);

        assertEquals("Your current balance = 800", result);
        assertEquals(800L, fromCard.getBalance());
        assertEquals(700L, toCard.getBalance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transfer_sameCard_throwsException() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("1111222233334444");
        dto.setAmount(100L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(dto));
        assertEquals("Cannot transfer to the same card", exception.getMessage());
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(2000L);

        when(cardRepository.findByCardNumberForUpdate("1111222233334444"))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumberForUpdate("5555666677778888"))
                .thenReturn(Optional.of(toCard));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(dto));
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void transfer_fromCardNotFound_throwsException() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("0000111122223333"); // несуществующая карта
        dto.setToCardNumber("5555666677778888");   // существующая карта
        dto.setAmount(100L);

        when(cardRepository.findByCardNumberForUpdate("0000111122223333"))
                .thenReturn(Optional.empty());
        when(cardRepository.findByCardNumberForUpdate("5555666677778888"))
                .thenReturn(Optional.of(toCard));

        assertThrows(CardNotFoundException.class,
                () -> transactionService.transfer(dto));
    }

    @Test
    void transfer_toCardNotFound_throwsException() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444"); // существующая карта
        dto.setToCardNumber("9999000011112222");   // несуществующая карта
        dto.setAmount(100L);

        when(cardRepository.findByCardNumberForUpdate("1111222233334444"))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumberForUpdate("9999000011112222"))
                .thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> transactionService.transfer(dto));
    }
}
