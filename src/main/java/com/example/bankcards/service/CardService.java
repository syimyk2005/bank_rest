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

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    /**
     * Создаёт новую карту для пользователя.
     *
     * @param cardRequestDto DTO с данными карты и ID пользователя
     * @return DTO созданной карты
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
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

    /**
     * Изменяет статус существующей карты.
     *
     * @param status DTO с ID карты и новым статусом
     * @return DTO обновлённой карты
     * @throws CardNotFoundException если карта с указанным ID не найдена
     */
    public CardResponseDto changeCardStatus(ChangeCardStatusDto status) {
        Card card = cardRepository.findById(status.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card with id: "
                        + status.getCardId() + " not found and you can't change status"));
        card.setStatus(status.getStatus());
        return cardMapper.toDto(cardRepository.save(card));
    }

    /**
     * Находит карту по ID.
     *
     * @param id ID карты
     * @return DTO найденной карты
     * @throws CardNotFoundException если карта не найдена
     */
    public CardResponseDto findCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + id + " not found or doesn't exist"));
        return cardMapper.toDto(card);
    }

    /**
     * Удаляет карту по ID.
     *
     * @param id ID карты
     * @return сообщение о результате удаления
     * @throws CardNotFoundException если карта не найдена
     */
    public String deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + id + " not found or was deleted before"));
        cardRepository.delete(card);
        return "Card with id: " + id + " has been deleted";
    }

    /**
     * Получает список всех карт.
     *
     * @return список DTO всех карт
     */
    public List<CardResponseDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    /**
     * Получает страницы карт текущего пользователя с возможностью поиска по номеру карты.
     * Номер карты в результатах замаскирован для безопасности.
     *
     * @param search строка для поиска по номеру карты (может быть пустой)
     * @param page номер страницы
     * @param size размер страницы
     * @return страница DTO карт пользователя
     * @throws UsernameNotFoundException если текущий пользователь не найден
     */
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
