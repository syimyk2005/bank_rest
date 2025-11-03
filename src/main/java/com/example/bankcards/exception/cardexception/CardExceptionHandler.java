package com.example.bankcards.exception.cardexception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

import static com.example.bankcards.util.ErrorResponseUtil.createErrorResponse;

/**
 * Глобальный обработчик исключений для операций с картами.
 * <p>
 * Перехватывает специфические исключения, связанные с картами,
 * и формирует удобный JSON-ответ с информацией о статусе, сообщением
 * и временной меткой.
 */
@Order(1)
@ControllerAdvice
public class CardExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CardExceptionHandler.class);

    /**
     * Обрабатывает ошибку недостаточного баланса.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        Map<String, Object> response = createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Обрабатывает ошибку дублирования номера карты.
     */
    @ExceptionHandler(CardNumberAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleCardNumberAlreadyExist(CardNumberAlreadyExistException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        Map<String, Object> response = createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Обрабатывает ошибку отсутствия карты.
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCardNotFoundException(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        Map<String, Object> response = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Обрабатывает ошибку доступа к чужой карте.
     */
    @ExceptionHandler(AccessDeniedForOtherCardException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedForOtherCardException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        Map<String, Object> response = createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
