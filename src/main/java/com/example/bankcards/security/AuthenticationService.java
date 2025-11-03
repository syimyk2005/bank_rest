package com.example.bankcards.security;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.TokenResponseDto;
import com.example.bankcards.dto.UserRequestDto;
import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.userexception.EmailAlreadyExistsException;
import com.example.bankcards.exception.userexception.UserNotFoundException;
import com.example.bankcards.exception.userexception.UsernameAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис аутентификации и регистрации пользователей.
 * Предоставляет методы для регистрации, аутентификации,
 * обновления токенов, сохранения и отзыва JWT токенов.
 */
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Регистрирует нового пользователя.
     * Проверяет, что username и email уникальны.
     * Хэширует пароль, сохраняет пользователя и возвращает JWT токены.
     *
     * @param request DTO с данными пользователя (username, email, password, role)
     * @return AuthResponse с access и refresh токенами
     * @throws UsernameAlreadyExistsException если username уже существует
     * @throws EmailAlreadyExistsException если email уже существует
     */
    public AuthResponse register(UserRequestDto request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("User with name: " + request.getUsername() + " already exists");
        }
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with email: " + request.getUsername() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = repository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveUserToken(accessToken, user);

        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * Аутентифицирует пользователя по username и паролю.
     * Генерирует новые JWT токены и отзывает старые.
     *
     * @param request DTO с username и password
     * @return AuthResponse с новыми access и refresh токенами
     * @throws UserNotFoundException если пользователь с указанным username не найден
     */
    public AuthResponse authenticate(UserRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with name: " + request.getUsername() + " does not exist"));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(accessToken, user);

        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * Обновляет access и refresh токены с использованием refresh токена.
     *
     * @param request HTTP запрос с заголовком Authorization: Bearer <refreshToken>
     * @return ResponseEntity с новым TokenResponseDto или UNAUTHORIZED, если токен некорректный
     */
    public ResponseEntity<TokenResponseDto> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtUtil.isValidRefreshToken(token, user)) {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, user);

            TokenResponseDto response = new TokenResponseDto(accessToken, refreshToken);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Сохраняет JWT токен пользователя в базе данных.
     *
     * @param tokenStr JWT токен
     * @param user пользователь, которому принадлежит токен
     */
    public void saveUserToken(String tokenStr, User user) {
        Token token = new Token();
        token.setAccessToken(tokenStr);
        token.setRevoked(false);
        token.setExpireDate(jwtUtil.extractExpiration(tokenStr));
        token.setUser(user);
        tokenRepository.save(token);
    }

    /**
     * Отзывает все активные токены пользователя.
     *
     * @param user пользователь, токены которого нужно отозвать
     */
    public void revokeAllTokenByUser(User user) {
        List<Token> validTokenListByUser = tokenRepository.findAllTokenByUser(user.getId());
        if (!validTokenListByUser.isEmpty()) {
            validTokenListByUser.forEach(t -> t.setRevoked(true));
        }
    }
}
