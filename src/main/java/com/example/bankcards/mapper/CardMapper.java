package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardForBlocking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardResponseDto toDto(Card card);

    Card toEntity(CardRequestDto cardRequestDto);

    CardForBlocking toBlockingEntity(CardBlockingRequest cardBlockingRequest);
}


