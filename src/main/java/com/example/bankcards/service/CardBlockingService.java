package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
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
        Card card = cardRepository.findById(cardBlockingRequest.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + cardBlockingRequest.getCardId() + " not found"));
        if (card.getStatus().equals(CardStatus.BLOCKED)) {
            return "Your card already blocked";
        }
        cardForBlockingRepository.save(cardMapper.toBlockingEntity(cardBlockingRequest));
        return "Request for blocking your card was sent";
    }


    @Transactional
    public String approveBlocking(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + id + " not found"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        cardForBlockingRepository.deleteById(id);
        return "Card with id: " + id + " has been blocked";
    }
}
