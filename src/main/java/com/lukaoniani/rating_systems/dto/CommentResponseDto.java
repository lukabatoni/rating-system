package com.lukaoniani.rating_systems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
