package org.example.boxy.auth_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.boxy.auth_service.mapper.UserMapper;
import org.example.boxy.auth_service.model.dto.UserRequestDto;
import org.example.boxy.auth_service.model.dto.UserResponseDto;
import org.example.boxy.auth_service.model.entity.User;
import org.example.boxy.auth_service.repository.TokenRepository;
import org.example.boxy.auth_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponseDto updateUser(Long id, UserRequestDto updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Transactional
    public void deleteById(Long id) {
        tokenRepository.deleteTokenByUserId(id);
        userRepository.deleteById(id);
    }
}
