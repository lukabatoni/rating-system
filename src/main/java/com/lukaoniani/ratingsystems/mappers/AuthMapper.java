package com.lukaoniani.ratingsystems.mappers;

import com.lukaoniani.ratingsystems.dto.AuthenticationRequestDto;
import com.lukaoniani.ratingsystems.dto.AuthenticationResponseDto;
import com.lukaoniani.ratingsystems.dto.RegisterRequestDto;
import com.lukaoniani.ratingsystems.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "emailConfirmed", ignore = true)
  @Mapping(target = "approved", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "password", ignore = true)
    // We'll handle password encoding separately
  User toEntity(RegisterRequestDto dto);

  // This could be useful if you needed to convert user data back to a request DTO
  AuthenticationRequestDto toAuthRequestDto(User user);

  // Create response with token
  @Mapping(source = "token", target = "token")
  AuthenticationResponseDto toAuthResponseDto(String token);
}