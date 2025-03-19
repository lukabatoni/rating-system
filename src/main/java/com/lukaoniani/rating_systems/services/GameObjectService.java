package com.lukaoniani.rating_systems.services;

import com.lukaoniani.rating_systems.dto.GameObjectRequestDto;
import com.lukaoniani.rating_systems.dto.GameObjectResponseDto;
import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.GameObject;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.GameObjectRepository;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameObjectService {
    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;

    public GameObjectResponseDto createGameObject(GameObjectRequestDto gameObjectRequestDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Ensure the user has the SELLER role
        if (user.getRole() != Role.SELLER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only sellers can create game objects");
        }

        GameObject gameObject = GameObject.builder()
                .title(gameObjectRequestDto.getTitle())
                .text(gameObjectRequestDto.getText())
                .user(user)
                .build();

        gameObject = gameObjectRepository.save(gameObject);

        return mapToDto(gameObject);
    }

    public List<GameObjectResponseDto> getAllGameObjects() {
        return gameObjectRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public GameObjectResponseDto updateGameObject(Integer gameObjectId, GameObjectRequestDto gameObjectRequestDto, Integer userId) {
        GameObject gameObject = gameObjectRepository.findById(gameObjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game object not found"));

        if (!gameObject.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own game objects");
        }

        gameObject.setTitle(gameObjectRequestDto.getTitle());
        gameObject.setText(gameObjectRequestDto.getText());

        gameObject = gameObjectRepository.save(gameObject);

        return mapToDto(gameObject);
    }

    public void deleteGameObject(Integer gameObjectId, Integer userId) {
        GameObject gameObject = gameObjectRepository.findById(gameObjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game object not found"));

        if (!gameObject.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own game objects");
        }

        gameObjectRepository.delete(gameObject);
    }

    private GameObjectResponseDto mapToDto(GameObject gameObject) {
        return GameObjectResponseDto.builder()
                .id(gameObject.getId())
                .title(gameObject.getTitle())
                .text(gameObject.getText())
                .userId(gameObject.getUser().getId())
                .userName(gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName())
                .createdAt(gameObject.getCreatedAt())
                .updatedAt(gameObject.getUpdatedAt())
                .build();
    }

}
