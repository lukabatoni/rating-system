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
public class SellerResponseDto {
  private Integer id;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDateTime createdAt;
  private boolean approved;
  private Double averageRating;
  private Integer commentCount;
}
