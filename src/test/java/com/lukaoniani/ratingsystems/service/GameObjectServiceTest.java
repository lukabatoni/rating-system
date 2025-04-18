package com.lukaoniani.ratingsystems.service;

import com.lukaoniani.ratingsystems.dto.GameObjectRequestDto;
import com.lukaoniani.ratingsystems.dto.GameObjectResponseDto;
import com.lukaoniani.ratingsystems.enums.Role;
import com.lukaoniani.ratingsystems.models.GameObject;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.GameObjectRepository;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import com.lukaoniani.ratingsystems.services.GameObjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameObjectServiceTest {
    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameObjectService gameObjectService;


    @Test
    public void testCreateGameObject_Success() {
        // Arrange
        GameObjectRequestDto request = new GameObjectRequestDto();
        request.setTitle("CS:GO Knife");
        request.setText("A rare knife skin for CS:GO.");

        User user = new User();
        user.setId(1);
        user.setRole(Role.SELLER);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(gameObjectRepository.save(any(GameObject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GameObjectResponseDto response = gameObjectService.createGameObject(request, 1);

        // Assert
        assertNotNull(response);
        assertEquals("CS:GO Knife", response.getTitle());
        assertEquals("A rare knife skin for CS:GO.", response.getText());
        assertEquals(1, response.getUserId());
    }

    @Test
    public void testCreateGameObject_UserNotSeller() {
        // Arrange
        GameObjectRequestDto request = new GameObjectRequestDto();
        request.setTitle("CS:GO Knife");
        request.setText("A rare knife skin for CS:GO.");

        User user = new User();
        user.setId(1);
        user.setRole(Role.USER);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameObjectService.createGameObject(request, 1);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Only sellers can create game objects", exception.getReason());
    }

    @Test
    public void testUpdateGameObject_Success() {
        // Arrange
        GameObjectRequestDto request = new GameObjectRequestDto();
        request.setTitle("Updated Title");
        request.setText("Updated Text");

        User user = new User();
        user.setId(1);

        GameObject gameObject = new GameObject();
        gameObject.setId(1);
        gameObject.setTitle("CS:GO Knife");
        gameObject.setText("A rare knife skin for CS:GO.");
        gameObject.setUser(user);

        when(gameObjectRepository.findById(1)).thenReturn(Optional.of(gameObject));
        when(gameObjectRepository.save(any(GameObject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GameObjectResponseDto response = gameObjectService.updateGameObject(1, request, 1);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Text", response.getText());
    }

    @Test
    public void testDeleteGameObject_Success() {
        // Arrange
        User user = new User();
        user.setId(1);

        GameObject gameObject = new GameObject();
        gameObject.setId(1);
        gameObject.setUser(user);

        when(gameObjectRepository.findById(1)).thenReturn(Optional.of(gameObject));

        // Act
        gameObjectService.deleteGameObject(1, 1);

        // Assert
        verify(gameObjectRepository, times(1)).delete(gameObject);
    }
}
