package com.lukaoniani.ratingsystems.mappers;

import com.lukaoniani.ratingsystems.dto.CommentRequestDto;
import com.lukaoniani.ratingsystems.dto.CommentResponseDto;
import com.lukaoniani.ratingsystems.models.Comment;
import com.lukaoniani.ratingsystems.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "author", ignore = true)
  @Mapping(target = "seller", ignore = true)
  @Mapping(target = "approved", constant = "false")
  @Mapping(target = "createdAt", ignore = true)
  Comment toEntity(CommentRequestDto dto);

  @Mapping(target = "authorId", expression = "java(comment.getAuthor() != null ? comment.getAuthor().getId() : null)")
  @Mapping(target = "authorName", expression = "java(getAuthorName(comment.getAuthor()))")
  CommentResponseDto toDto(Comment comment);

  // Helper method to determine author name
  default String getAuthorName(User author) {
    return author != null ?
        author.getFirstName() + " " + author.getLastName() :
        "Anonymous";
  }

  // Update an existing comment from DTO
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCommentFromDto(CommentRequestDto dto, @MappingTarget Comment comment);
}