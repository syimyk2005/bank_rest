package org.example.boxy.auth_service.mapper;


import org.example.boxy.auth_service.model.dto.UserRequestDto;
import org.example.boxy.auth_service.model.dto.UserResponseDto;
import org.example.boxy.auth_service.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toModel(UserRequestDto dto);

}





