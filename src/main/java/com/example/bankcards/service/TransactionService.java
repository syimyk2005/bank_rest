package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.cardexception.AccessDeniedForOtherCardException;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    public String transfer(TransferRequestDto dto) {
        if (dto.getFromCardNumber().equals(dto.getToCardNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Card fromCard = cardRepository.findByCardNumberForUpdate(dto.getFromCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Source card not found"));

        Card toCard = cardRepository.findByCardNumberForUpdate(dto.getToCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Target card not found"));

        if (!fromCard.getUser().equals(currentUser)) {
            throw new AccessDeniedForOtherCardException("You can transfer money only from your own cards");
        }

        if (!toCard.getUser().equals(currentUser)) {
            throw new AccessDeniedForOtherCardException("You can transfer money only to your own cards");
        }

        if (fromCard.getBalance() < dto.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        fromCard.setBalance(fromCard.getBalance() - dto.getAmount());
        toCard.setBalance(toCard.getBalance() + dto.getAmount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return "Your current balances: " +
                "fromCard = " + fromCard.getBalance() +
                " toCard = " + toCard.getBalance();
    }

}
