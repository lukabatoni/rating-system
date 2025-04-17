package com.lukaoniani.ratingsystems.mappers;

import com.lukaoniani.ratingsystems.dto.SellerRequestDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.models.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "approved", ignore = true)
  @Mapping(target = "emailConfirmed", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  User toEntity(SellerRequestDto dto);

  @Mapping(target = "averageRating", ignore = true)
  @Mapping(target = "commentCount", ignore = true)
  SellerResponseDto toDto(User user);

  @AfterMapping
  default void setRatingAndCommentCount(User user, @MappingTarget SellerResponseDto dto,
                                        @Context Double averageRating,
                                        @Context Integer commentCount) {
    dto.setAverageRating(averageRating != null ? averageRating : 0.0);
    dto.setCommentCount(commentCount);
  }
}