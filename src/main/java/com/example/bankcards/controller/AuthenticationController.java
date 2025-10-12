package org.example.boxy.auth_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxy.auth_service.model.dto.AuthResponse;
import org.example.boxy.auth_service.model.dto.UserRequestDto;
import org.example.boxy.auth_service.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Регистрация, вход и обновление токена")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация нового пользователя", description = "Создаёт нового пользователя и возвращает Access и Refresh токены")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(summary = "Эндпоинт входа пользователя", description = "Аутентифицирует пользователя и возвращает токены")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(summary = "Эндпоинт обновления токена", description = "Обновляет access токен с помощью refresh токена")
    @PostMapping("/refresh-token")
    public ResponseEntity refreshToken(HttpServletRequest request) {
        return authenticationService.refresh(request);
    }
}
