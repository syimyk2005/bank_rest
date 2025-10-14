package com.example.bankcards.mapper;


import com.example.bankcards.dto.UserRequestDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // @Mapping(target = "role", source = "role")
    UserResponseDto toDto(User user);

    User toEntity(UserRequestDto dto);
}





