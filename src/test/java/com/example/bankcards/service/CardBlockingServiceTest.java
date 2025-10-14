package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardForBlocking;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardForBlockingRepository;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardBlockingServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardForBlockingRepository cardForBlockingRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardBlockingService cardBlockingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void requestCardBlocking_activeCard_sendsRequest() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        CardForBlocking blocking = new CardForBlocking();
        when(cardMapper.toBlockingEntity(any(CardBlockingRequest.class))).thenReturn(blocking);

        CardBlockingRequest request = new CardBlockingRequest();
        request.setCardId(1L);

        String result = cardBlockingService.requestCardBlocking(request);
        assertEquals("Request for blocking your card was sent", result);
        verify(cardForBlockingRepository).save(blocking);
    }

    @Test
    void requestCardBlocking_cardAlreadyBlocked_returnsMessage() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardBlockingRequest request = new CardBlockingRequest();
        request.setCardId(1L);

        String result = cardBlockingService.requestCardBlocking(request);
        assertEquals("Your card already blocked", result);
        verify(cardForBlockingRepository, never()).save(any());
    }

    @Test
    void approveBlocking_existingCard_blocksCard() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        String result = cardBlockingService.approveBlocking(1L);
        assertEquals("Card with id: 1 has been blocked", result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());

        verify(cardRepository).save(card);
        verify(cardForBlockingRepository).deleteById(1L);
    }

    @Test
    void approveBlocking_cardNotFound_throwsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardBlockingService.approveBlocking(1L));
        verify(cardRepository, never()).save(any());
        verify(cardForBlockingRepository, never()).deleteById(any());
    }
}
