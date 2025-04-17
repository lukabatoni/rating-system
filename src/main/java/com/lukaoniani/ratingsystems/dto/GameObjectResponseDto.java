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
public class GameObjectResponseDto {
  private Integer id;
  private String title;
  private String text;
  private Integer userId;
  private String userName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
