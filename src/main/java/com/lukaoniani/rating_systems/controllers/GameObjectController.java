package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.GameObjectRequestDto;
import com.lukaoniani.rating_systems.dto.GameObjectResponseDto;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.services.GameObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/objects")
@RequiredArgsConstructor
public class GameObjectController {
    private final GameObjectService gameObjectService;

    @PostMapping
    public ResponseEntity<GameObjectResponseDto> createGameObject(
            @RequestBody GameObjectRequestDto gameObjectRequestDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gameObjectService.createGameObject(gameObjectRequestDto, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<GameObjectResponseDto>> getAllGameObjects() {
        return ResponseEntity.ok(gameObjectService.getAllGameObjects());
    }

    @PutMapping("/{objectId}")
    public ResponseEntity<GameObjectResponseDto> updateGameObject(
            @PathVariable Integer objectId,
            @RequestBody GameObjectRequestDto gameObjectRequestDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gameObjectService.updateGameObject(objectId, gameObjectRequestDto, user.getId()));
    }

    @DeleteMapping("/{objectId}")
    public ResponseEntity<Void> deleteGameObject(
            @PathVariable Integer objectId,
            @AuthenticationPrincipal User user) {
        gameObjectService.deleteGameObject(objectId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
