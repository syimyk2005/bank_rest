package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequestDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер, отвечающий за управление пользователями.
 * <p>
 * Содержит эндпоинты для получения всех пользователей, поиска конкретного пользователя и обновления данных пользователя.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Возвращает список всех пользователей.
     *
     * @return список объектов {@link UserResponseDto} с информацией о пользователях
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> allUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link UserResponseDto} с информацией о пользователе
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id   идентификатор пользователя, которого нужно обновить
     * @param user объект {@link UserRequestDto} с новыми данными пользователя
     * @return объект {@link UserResponseDto} с обновлённой информацией о пользователе
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
}