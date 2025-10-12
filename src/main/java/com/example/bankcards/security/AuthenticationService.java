package org.example.boxy.auth_service.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.boxy.auth_service.mapper.UserMapper;
import org.example.boxy.auth_service.model.dto.AuthResponse;
import org.example.boxy.auth_service.model.dto.TokenResponse;
import org.example.boxy.auth_service.model.dto.UserRequestDto;
import org.example.boxy.auth_service.model.entity.Token;
import org.example.boxy.auth_service.model.entity.User;
import org.example.boxy.auth_service.repository.TokenRepository;
import org.example.boxy.auth_service.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthResponse register(UserRequestDto request) {

        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = repository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(accessToken, user);

        return new AuthResponse(request.getUsername(), accessToken, refreshToken);
    }

    public AuthResponse authenticate(UserRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(accessToken, user);
        return new AuthResponse(request.getUsername(), accessToken, refreshToken);

    }

    public ResponseEntity refresh(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
        if (jwtService.isValidRefreshToken(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, user);

            return new ResponseEntity(new TokenResponse(accessToken, refreshToken), HttpStatus.OK);
        }
        return new ResponseEntity((HttpStatus.UNAUTHORIZED));
    }

    public void saveUserToken(String tokenStr, User user) {
        Token token = new Token();
        token.setToken(tokenStr);
        token.setRevoked(false);
        token.setExpireDate(jwtService.extractExpiration(tokenStr));
        token.setUser(user);
        tokenRepository.save(token);
    }

    public void revokeAllTokenByUser(User user) {
        List<Token> validTokenListByUser = tokenRepository.findAllTokenByUser(user.getId());
        if (!validTokenListByUser.isEmpty()) {
            validTokenListByUser.forEach(t -> {
                t.setRevoked(true);
            });
        }
    }

}
