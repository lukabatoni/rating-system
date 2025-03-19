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
public class GameObjectResponseDto {
    private Integer id;
    private String title;
    private String text;
    private Integer userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
