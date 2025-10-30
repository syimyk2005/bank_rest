package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardForBlocking;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.CardNumberAlreadyExistException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardForBlockingRepository;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CardBlockingServiceTest {

    @Mock
    private CardForBlockingRepository cardForBlockingRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardForBlocking cardForBlocking;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardBlockingService service;

    private AutoCloseable closeable;
    private Card card;
    private CardBlockingRequest request;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        card = new Card();
        card.setCardNumber("1111222233334444");
        card.setStatus(CardStatus.ACTIVE);
        request = new CardBlockingRequest("1111222233334444", "comment");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void requestCardBlocking_success() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(card));

        String result = service.requestCardBlocking(request);

        assertEquals("Request for blocking your card was sent", result);
        verify(cardForBlockingRepository).save(any());
    }


    @Test
    void requestCardBlocking_cardAlreadyRequested_throwsException() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(cardForBlocking));

        assertThrows(CardNumberAlreadyExistException.class,
                () -> service.requestCardBlocking(request));
    }

    @Test
    void requestCardBlocking_cardNotFound_throwsException() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> service.requestCardBlocking(request));
    }

    @Test
    void requestCardBlocking_alreadyBlocked_returnsMessage() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(card));

        String result = service.requestCardBlocking(request);

        assertEquals("Your card already blocked", result);
        verify(cardForBlockingRepository, never()).save(any());
    }

    @Test
    void approveBlocking_success() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(cardForBlocking));
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(card));

        String result = service.approveBlocking(card.getCardNumber());

        assertEquals("Card with id: 1111222233334444 has been blocked", result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
        verify(cardForBlockingRepository).deleteByCardNumber(card.getCardNumber());
    }

    @Test
    void approveBlocking_notRequested_throwsException() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());

        String cardNumber = card.getCardNumber();
        assertThrows(CardNotFoundException.class,
                () -> service.approveBlocking(cardNumber));
    }

    @Test
    void approveBlocking_cardNotFound_throwsException() {
        when(cardForBlockingRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(cardForBlocking));
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.empty());

        String cardNumber = card.getCardNumber();
        assertThrows(CardNotFoundException.class,
                () -> service.approveBlocking(cardNumber));
    }

}
