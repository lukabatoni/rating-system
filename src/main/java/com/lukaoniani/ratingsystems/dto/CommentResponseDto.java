package com.lukaoniani.ratingsystems.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
  private Integer id;
  private String message;
  private Integer rating;
  private Integer authorId;
  private String authorName;
  private LocalDateTime createdAt;
  private boolean approved;
}
