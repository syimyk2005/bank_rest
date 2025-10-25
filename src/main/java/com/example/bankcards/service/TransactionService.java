package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.cardexception.AccessDeniedForOtherCardException;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Сервис для выполнения денежных переводов между картами пользователя.
 */
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final CardRepository cardRepository;

    /**
     * Переводит указанную сумму с одной карты на другую.
     *
     * @param dto объект TransferRequestDto с номером карты-отправителя,
     *            номером карты-получателя и суммой перевода
     * @return строка с текущими балансами обеих карт после перевода
     * @throws IllegalArgumentException если номера карт совпадают
     * @throws CardNotFoundException если исходная или целевая карта не найдена
     * @throws AccessDeniedForOtherCardException если карты принадлежат другому пользователю
     * @throws InsufficientBalanceException если на исходной карте недостаточно средств
     */
    @Transactional
    public String transfer(TransferRequestDto dto) {
        if (dto.getFromCardNumber().equals(dto.getToCardNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Card> cards = cardRepository.findAllByCardNumberInForUpdate(
                Arrays.asList(dto.getFromCardNumber(), dto.getToCardNumber())
        );

        Card fromCard = cards.stream()
                .filter(c -> c.getCardNumber().equals(dto.getFromCardNumber()))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException("Source card not found"));

        Card toCard = cards.stream()
                .filter(c -> c.getCardNumber().equals(dto.getToCardNumber()))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException("Target card not found"));

        if (!fromCard.getUser().equals(currentUser) || !toCard.getUser().equals(currentUser)) {
            throw new AccessDeniedForOtherCardException("You can transfer only between your own cards");
        }

        if (fromCard.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(dto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(dto.getAmount()));

        return "Your current balances: fromCard = " + fromCard.getBalance() +
                " toCard = " + toCard.getBalance();
    }
}
