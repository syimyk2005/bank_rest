package com.example.bankcards.config;

import com.example.bankcards.entity.Token;
import com.example.bankcards.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Обработчик выхода из системы.
 * <p>
 * При выходе помечает токен как отозванный (revoked),
 * чтобы его больше нельзя было использовать.
 */
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Помечает токен из заголовка Authorization как отозванный.
     *
     * @param request        HTTP-запрос с токеном
     * @param response       HTTP-ответ
     * @param authentication информация об аутентификации пользователя
     */
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7).trim();
        Token storedToken = tokenRepository.findByAccessToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
    }
}
