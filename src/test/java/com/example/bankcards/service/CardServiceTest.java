package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ChangeCardStatusDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.cardexception.CardNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createCard_returnsDto() {
        CardRequestDto request = new CardRequestDto();
        request.setUser(1L);

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        CardResponseDto dto = new CardResponseDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(request)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(dto);

        CardResponseDto result = cardService.createCard(request);

        assertThat(result).isNotNull();
        verify(cardRepository).save(card);
    }


    @Test
    void changeCardStatus_updatesStatus() {
        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        CardResponseDto dto = new CardResponseDto();
        ChangeCardStatusDto statusDto = new ChangeCardStatusDto();
        statusDto.setCardId(1L);
        statusDto.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(dto);

        CardResponseDto result = cardService.changeCardStatus(statusDto);

        assertThat(result).isNotNull();
        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void changeCardStatus_cardNotFound_throwsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        ChangeCardStatusDto statusDto = new ChangeCardStatusDto();
        statusDto.setCardId(1L);

        assertThrows(CardNotFoundException.class,
                () -> cardService.changeCardStatus(statusDto));
    }

    @Test
    void findCard_returnsDto() {
        Card card = new Card();
        CardResponseDto dto = new CardResponseDto();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(dto);

        CardResponseDto result = cardService.findCard(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void findCard_cardNotFound_throwsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.findCard(1L));
    }

    @Test
    void deleteCard_removesCard() {
        Card card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        String result = cardService.deleteCard(1L);

        verify(cardRepository).delete(card);
        assertThat(result).contains("has been deleted");
    }

    @Test
    void getAllCards_returnsDtos() {
        Card card = new Card();
        CardResponseDto dto = new CardResponseDto();
        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(cardMapper.toDto(card)).thenReturn(dto);

        List<CardResponseDto> result = cardService.getAllCards();

        assertThat(result).hasSize(1);
    }

    @Test
    void findCardsForCurrentUser_masksCardNumbers() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Card card = new Card();
        card.setCardNumber("1234567812345678");

        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(page);

        CardResponseDto dto = new CardResponseDto();
        dto.setCardNumber(card.getCardNumber());
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        Page<CardResponseDto> result = cardService.findCardsForCurrentUser("", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getCardNumber())
                .isEqualTo(CardUtil.maskPan("1234567812345678"));
    }

    @Test
    void findCardsForCurrentUser_withSearch() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Card card = new Card();
        card.setCardNumber("1234567812345678");

        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findByUserIdAndCardNumberContaining(eq(1L), eq("1234"), any(Pageable.class)))
                .thenReturn(page);

        CardResponseDto dto = new CardResponseDto();
        dto.setCardNumber(card.getCardNumber());
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        Page<CardResponseDto> result = cardService.findCardsForCurrentUser("1234", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getCardNumber())
                .isEqualTo(CardUtil.maskPan("1234567812345678"));
    }

    @Test
    void findCardsForCurrentUser_userNotFound_throwsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> cardService.findCardsForCurrentUser("", 0, 10));
    }
}
