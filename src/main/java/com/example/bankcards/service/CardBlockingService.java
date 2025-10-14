package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.CardNumberAlreadyExistException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardForBlockingRepository;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardBlockingService {
    private final CardForBlockingRepository cardForBlockingRepository;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public String requestCardBlocking(CardBlockingRequest cardBlockingRequest) {
        if (cardForBlockingRepository.findByCardNumber(cardBlockingRequest.getCardNumber()).isPresent()) {
            throw new CardNumberAlreadyExistException("Card was requested for blocking already");
        }
        Card card = cardRepository.findByCardNumber(cardBlockingRequest.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card with number: " + cardBlockingRequest.getCardNumber() + " not found"));
        if (card.getStatus().equals(CardStatus.BLOCKED)) {
            return "Your card already blocked";
        }
        cardForBlockingRepository.save(cardMapper.toBlockingEntity(cardBlockingRequest));
        return "Request for blocking your card was sent";
    }


    @Transactional
    public String approveBlocking(String cardNumber) {
        cardForBlockingRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card with number: " + cardNumber + " was not requested for blocking"));

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + cardNumber + " not found"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        cardForBlockingRepository.deleteByCardNumber(cardNumber);
        return "Card with id: " + cardNumber + " has been blocked";
    }
}
