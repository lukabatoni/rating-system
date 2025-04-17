package com.lukaoniani.ratingsystems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerRequestDto {
  private String firstName;
  private String lastName;
  private String email;
}
