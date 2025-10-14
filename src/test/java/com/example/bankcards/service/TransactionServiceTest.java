package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

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
        fromCard.setBalance(1000.0);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setCardNumber("5555666677778888");
        toCard.setBalance(500.0);
    }


    @Test
    void transfer_successful() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            User currentUser = new User();
            currentUser.setUsername("testuser");
            fromCard.setUser(currentUser);
            toCard.setUser(currentUser);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
            when(cardRepository.findByCardNumberForUpdate("1111222233334444")).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByCardNumberForUpdate("5555666677778888")).thenReturn(Optional.of(toCard));

            TransferRequestDto dto = new TransferRequestDto();
            dto.setFromCardNumber("1111222233334444");
            dto.setToCardNumber("5555666677778888");
            dto.setAmount(200.0);

            String result = transactionService.transfer(dto);

            assertTrue(result.contains("fromCard = 800.0"));
            assertTrue(result.contains("toCard = 700.0"));

            assertEquals(800.0, fromCard.getBalance(), 0.001);
            assertEquals(700.0, toCard.getBalance(), 0.001);

            verify(cardRepository).save(fromCard);
            verify(cardRepository).save(toCard);
        }
    }


    @Test
    void transfer_sameCard_throwsException() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardNumber("1111222233334444");
        dto.setToCardNumber("1111222233334444");
        dto.setAmount(100.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(dto));
        assertEquals("Cannot transfer to the same card", exception.getMessage());
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User currentUser = new User();
            currentUser.setUsername("testuser");
            fromCard.setUser(currentUser);
            toCard.setUser(currentUser);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
            when(cardRepository.findByCardNumberForUpdate("1111222233334444")).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByCardNumberForUpdate("5555666677778888")).thenReturn(Optional.of(toCard));

            TransferRequestDto dto = new TransferRequestDto();
            dto.setFromCardNumber("1111222233334444");
            dto.setToCardNumber("5555666677778888");
            dto.setAmount(2000.0);

            InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class,
                    () -> transactionService.transfer(dto));
            assertEquals("Insufficient balance", exception.getMessage());
        }
    }


    @Test
    void transfer_fromCardNotFound_throwsException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User currentUser = new User();
            currentUser.setUsername("testuser");
            toCard.setUser(currentUser);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
            when(cardRepository.findByCardNumberForUpdate("0000111122223333")).thenReturn(Optional.empty());
            when(cardRepository.findByCardNumberForUpdate("5555666677778888")).thenReturn(Optional.of(toCard));

            TransferRequestDto dto = new TransferRequestDto();
            dto.setFromCardNumber("0000111122223333");
            dto.setToCardNumber("5555666677778888");
            dto.setAmount(100.0);

            assertThrows(CardNotFoundException.class, () -> transactionService.transfer(dto));
        }
    }

    @Test
    void transfer_toCardNotFound_throwsException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User currentUser = new User();
            currentUser.setUsername("testuser");
            fromCard.setUser(currentUser);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
            when(cardRepository.findByCardNumberForUpdate("1111222233334444")).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByCardNumberForUpdate("9999000011112222")).thenReturn(Optional.empty());

            TransferRequestDto dto = new TransferRequestDto();
            dto.setFromCardNumber("1111222233334444");
            dto.setToCardNumber("9999000011112222");
            dto.setAmount(100.0);

            assertThrows(CardNotFoundException.class, () -> transactionService.transfer(dto));
        }
    }

}
