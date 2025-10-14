package com.example.bankcards.controller;

import com.example.bankcards.controller.CardBlockingController;
import com.example.bankcards.service.CardBlockingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardBlockingControllerTest {

    private MockMvc mockMvc;
    private CardBlockingService cardBlockingService;
    private CardBlockingController controller;

    @BeforeEach
    void setup() {
        cardBlockingService = Mockito.mock(CardBlockingService.class);
        controller = new CardBlockingController(cardBlockingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void requestBlocking_shouldReturnOk() throws Exception {
        Mockito.when(cardBlockingService.requestCardBlocking(any()))
                .thenReturn("Card blocking requested");

        mockMvc.perform(post("/api/cards/blocking/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":1,\"comment\":\"Lost card\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card blocking requested"));
    }

    @Test
    void approveBlocking_shouldReturnOk() throws Exception {
        Mockito.when(cardBlockingService.approveBlocking(1L))
                .thenReturn("Card blocking approved");

        mockMvc.perform(post("/api/cards/blocking/approve/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card blocking approved"));
    }
}
