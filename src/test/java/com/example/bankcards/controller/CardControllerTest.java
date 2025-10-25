package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ChangeCardStatusDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerTest {

    private MockMvc mockMvc;
    private CardService cardService;
    private ObjectMapper objectMapper;

    private CardRequestDto validRequest;
    private CardResponseDto validResponse;

    @BeforeEach
    void setUp() {
        cardService = mock(CardService.class);
        CardController cardController = new CardController(cardService);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validRequest = new CardRequestDto(
                "1234567812345678",
                1L,
                LocalDate.now().plusYears(2),
                CardStatus.ACTIVE,
                new BigDecimal("1000.0")
        );

        validResponse = new CardResponseDto(
                1L,
                validRequest.getCardNumber(),
                validRequest.getUser(),
                validRequest.getExpirationDate(),
                validRequest.getStatus(),
                validRequest.getBalance()
        );
    }

    @Test
    void createCard_shouldReturnCreated() throws Exception {
        when(cardService.createCard(any(CardRequestDto.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("1234567812345678"))
                .andExpect(jsonPath("$.user").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void findCard_shouldReturnOk() throws Exception {
        when(cardService.findCard(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234567812345678"))
                .andExpect(jsonPath("$.user").value(1L));
    }

    @Test
    void getAllCards_shouldReturnOk() throws Exception {
        when(cardService.getAllCards()).thenReturn(List.of(validResponse));

        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardNumber").value("1234567812345678"))
                .andExpect(jsonPath("$[0].user").value(1L));
    }

    @Test
    void changeCardStatus_shouldReturnOk() throws Exception {
        ChangeCardStatusDto statusDto = new ChangeCardStatusDto();
        statusDto.setCardId(1L);
        statusDto.setStatus(CardStatus.BLOCKED);

        CardResponseDto updatedResponse = new CardResponseDto(
                validResponse.getId(),
                validResponse.getCardNumber(),
                validResponse.getUser(),
                validResponse.getExpirationDate(),
                CardStatus.BLOCKED,
                validResponse.getBalance()
        );

        when(cardService.changeCardStatus(any(ChangeCardStatusDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/cards/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        when(cardService.deleteCard(1L)).thenReturn("Card deleted");

        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card deleted"));
    }
}
