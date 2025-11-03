package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ChangeCardStatusDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.exception.cardexception.CardNumberAlreadyExistException;
import com.example.bankcards.exception.userexception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с картами пользователей.
 * Предоставляет методы для создания, изменения статуса, поиска, удаления и получения карт.
 */
@RequiredArgsConstructor
@Service
public class CardService {

    private static final String CARD_WITH_ID = "Card with id: ";

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    public CardResponseDto createCard(CardRequestDto cardRequestDto) {
        userRepository.findById(cardRequestDto.getUser())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + cardRequestDto.getUser()));

        if (cardRepository.existsByCardNumber(cardRequestDto.getCardNumber())) {
            throw new CardNumberAlreadyExistException("Card number already exists");
        }

        return cardMapper.toDto(
                cardRepository.save(cardMapper.toEntity(cardRequestDto))
        );
    }

    public CardResponseDto changeCardStatus(ChangeCardStatusDto status) {
        Card card = cardRepository.findById(status.getCardId())
                .orElseThrow(() -> new CardNotFoundException(
                        CARD_WITH_ID + status.getCardId() + " not found and you can't change status"
                ));
        card.setStatus(status.getStatus());
        return cardMapper.toDto(cardRepository.save(card));
    }

    public CardResponseDto findCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(
                        CARD_WITH_ID + id + " not found or doesn't exist"
                ));
        return cardMapper.toDto(card);
    }

    public String deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(CARD_WITH_ID + id + " not found or was deleted before");
        }
        cardRepository.deleteById(id);
        return CARD_WITH_ID + id + " has been deleted";
    }

    public List<CardResponseDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    public Page<CardResponseDto> findCardsForCurrentUser(String search, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Pageable pageable = PageRequest.of(page, size);

        Page<Card> cardsPage;
        if (search != null && !search.isEmpty()) {
            cardsPage = cardRepository.findByUserIdAndCardNumberContaining(user.getId(), search, pageable);
        } else {
            cardsPage = cardRepository.findByUserId(user.getId(), pageable);
        }

        return cardsPage.map(card -> {
            CardResponseDto dto = cardMapper.toDto(card);
            dto.setCardNumber(CardUtil.maskPan(dto.getCardNumber()));
            return dto;
        });
    }
}
