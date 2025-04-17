package com.lukaoniani.ratingsystems.controllers;

import com.lukaoniani.ratingsystems.dto.GameObjectRequestDto;
import com.lukaoniani.ratingsystems.dto.GameObjectResponseDto;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.services.GameObjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/objects")
@RequiredArgsConstructor
@Slf4j
public class GameObjectController {

  private final GameObjectService gameObjectService;

  // Create a new GameObject
  @PostMapping
  public ResponseEntity<GameObjectResponseDto> createGameObject(
      @RequestBody GameObjectRequestDto gameObjectRequestDto,
      @AuthenticationPrincipal User user) {

    log.debug("Request to create a new game object. UserId: {}, GameObjectRequestDto: {}",
        user.getId(), gameObjectRequestDto);

    GameObjectResponseDto createdObject = gameObjectService.createGameObject(gameObjectRequestDto, user.getId());

    log.info("Game object created successfully. ObjectId: {}", createdObject.getId());
    return ResponseEntity.ok(createdObject);
  }

  // Get all GameObjects
  @GetMapping
  public ResponseEntity<List<GameObjectResponseDto>> getAllGameObjects() {
    log.debug("Fetching all game objects.");

    List<GameObjectResponseDto> allObjects = gameObjectService.getAllGameObjects();

    log.info("Fetched {} game objects.", allObjects.size());
    return ResponseEntity.ok(allObjects);
  }

  // Update a specific GameObject
  @PutMapping("/{objectId}")
  public ResponseEntity<GameObjectResponseDto> updateGameObject(
      @PathVariable Integer objectId,
      @RequestBody GameObjectRequestDto gameObjectRequestDto,
      @AuthenticationPrincipal User user) {

    log.debug("Request to update game object with ID: {}. UserId: {}, GameObjectRequestDto: {}",
        objectId, user.getId(), gameObjectRequestDto);

    GameObjectResponseDto updatedObject =
        gameObjectService.updateGameObject(objectId, gameObjectRequestDto, user.getId());

    log.info("Game object with ID: {} updated successfully.", objectId);
    return ResponseEntity.ok(updatedObject);
  }

  // Delete a specific GameObject
  @DeleteMapping("/{objectId}")
  public ResponseEntity<Void> deleteGameObject(
      @PathVariable Integer objectId,
      @AuthenticationPrincipal User user) {

    log.debug("Request to delete game object with ID: {}. UserId: {}", objectId, user.getId());

    gameObjectService.deleteGameObject(objectId, user.getId());

    log.info("Game object with ID: {} deleted successfully.", objectId);
    return ResponseEntity.noContent().build();
  }
}
