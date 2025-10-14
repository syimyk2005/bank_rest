package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ChangeCardStatusDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
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

@RequiredArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    public CardResponseDto createCard(CardRequestDto cardRequestDto) {
        return cardMapper.toDto(
                cardRepository.save(
                        cardMapper.toEntity(cardRequestDto)
                )
        );
    }

    public CardResponseDto changeCardStatus(ChangeCardStatusDto status) {
        Card card = cardRepository.findById(status.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + status.getCardId() + " not found"));
        card.setStatus(status.getStatus());
        return cardMapper.toDto(cardRepository.save(card));
    }

    public CardResponseDto findCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + id + " not found"));
        return cardMapper.toDto(card);
    }

    public String deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + id + " not found"));
        cardRepository.delete(card);
        return "Card with id: " + id + " has been deleted";
    }

    public List<CardResponseDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    public Page<CardResponseDto> getCardsForCurrentUser(String search, int page, int size) {
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
