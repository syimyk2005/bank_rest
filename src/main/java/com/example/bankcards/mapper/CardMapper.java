package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardForBlocking;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "user", source = "user")
    CardResponseDto toDto(Card card);

    @Mapping(target = "user", source = "user")
    Card toEntity(CardRequestDto cardRequestDto);

    CardForBlocking toBlockingEntity(CardBlockingRequest cardBlockingRequest);

    default User map(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Long map(User user) {
        return user != null ? user.getId() : null;
    }
}