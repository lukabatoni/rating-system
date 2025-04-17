package com.lukaoniani.ratingsystems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
  private String message;
  private Integer rating;
  private Integer sellerId;
  private String sellerFirstName;
  private String sellerLastName;
  private String sellerEmail;
}
