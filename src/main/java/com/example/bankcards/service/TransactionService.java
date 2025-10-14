package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final CardRepository cardRepository;

    @Transactional
    public String transfer(TransferRequestDto dto) {
        if (dto.getFromCardNumber().equals(dto.getToCardNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        Card fromCard = cardRepository.findByCardNumberForUpdate(dto.getFromCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Source card not found"));

        Card toCard = cardRepository.findByCardNumberForUpdate(dto.getToCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Target card not found"));

        if (fromCard.getBalance() < dto.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        fromCard.setBalance(fromCard.getBalance() - dto.getAmount());
        toCard.setBalance(toCard.getBalance() + dto.getAmount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return "Your current balance = " + fromCard.getBalance();
    }
}
