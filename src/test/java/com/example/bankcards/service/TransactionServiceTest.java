package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.cardexception.AccessDeniedForOtherCardException;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private CardRepository cardRepository;
    private TransactionService transactionService;

    private User currentUser;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        transactionService = new TransactionService(cardRepository);

        currentUser = new User();
        currentUser.setUsername("testuser");

        fromCard = new Card();
        fromCard.setCardNumber("1111222233334444");
        fromCard.setUser(currentUser);
        fromCard.setBalance(BigDecimal.valueOf(1000.00));

        toCard = new Card();
        toCard.setCardNumber("5555666677778888");
        toCard.setUser(currentUser);
        toCard.setBalance(BigDecimal.valueOf(500.00));
    }

    private void mockSecurityContext(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void transfer_successful() {
        mockSecurityContext(currentUser);
        when(cardRepository.findAllByCardNumberInForUpdate(anyList()))
                .thenReturn(List.of(fromCard, toCard));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(BigDecimal.valueOf(200.00));

        String result = transactionService.transfer(dto);

        assertEquals(0, fromCard.getBalance().compareTo(BigDecimal.valueOf(800.00)));
        assertEquals(0, toCard.getBalance().compareTo(BigDecimal.valueOf(700.00)));
        assertTrue(result.contains("fromCard = 800.0"));
        assertTrue(result.contains("toCard = 700.0"));
    }

    @Test
    void transfer_sameCard_throwsException() {
        mockSecurityContext(currentUser);

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("1111222233334444");
        dto.setAmount(BigDecimal.valueOf(100.00));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(dto));

        assertEquals("Cannot transfer to the same card", ex.getMessage());
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        mockSecurityContext(currentUser);
        fromCard.setBalance(BigDecimal.valueOf(100.00));

        when(cardRepository.findAllByCardNumberInForUpdate(anyList()))
                .thenReturn(List.of(fromCard, toCard));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(BigDecimal.valueOf(200.00));

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class,
                () -> transactionService.transfer(dto));

        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void transfer_cardNotFound_throwsException() {
        mockSecurityContext(currentUser);

        when(cardRepository.findAllByCardNumberInForUpdate(anyList()))
                .thenReturn(List.of(toCard));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(BigDecimal.valueOf(100.00));

        CardNotFoundException ex = assertThrows(CardNotFoundException.class,
                () -> transactionService.transfer(dto));

        assertEquals("Source card not found", ex.getMessage());
    }

    @Test
    void transfer_accessDeniedForOtherCard_throwsException() {
        currentUser.setId(1L);
        currentUser.setUsername("owner");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("intruder");

        mockSecurityContext(currentUser);

        fromCard.setUser(anotherUser);
        toCard.setUser(currentUser);

        when(cardRepository.findAllByCardNumberInForUpdate(anyList()))
                .thenReturn(List.of(fromCard, toCard));

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("5555666677778888");
        dto.setAmount(BigDecimal.valueOf(100.00));

        AccessDeniedForOtherCardException ex = assertThrows(
                AccessDeniedForOtherCardException.class,
                () -> transactionService.transfer(dto)
        );

        assertEquals("You can transfer only between your own cards", ex.getMessage());
    }

}
