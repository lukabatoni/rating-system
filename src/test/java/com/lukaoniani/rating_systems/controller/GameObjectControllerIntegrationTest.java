package com.lukaoniani.rating_systems.controller;

import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.GameObject;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.GameObjectRepository;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class GameObjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("rating_system")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setUp() {
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();

        seller = User.builder()
                .firstName("luka")
                .lastName("oniani")
                .email("lukaa.oniani2002@gmail.com")
                .password("password")
                .role(Role.SELLER)
                .approved(true)
                .emailConfirmed(true)
                .createdAt(LocalDateTime.now())
                .build();

        seller = userRepository.save(seller); // Save the seller and assign the returned object

        assertNotNull(seller, "Seller object should not be null after saving");

        GameObject gameObject = GameObject.builder()
                .title("FIFA")
                .text("New Ultimate Team")
                .user(seller)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        gameObjectRepository.save(gameObject);
    }

    @Test
    public void testGetAllGameObjects() throws Exception {
        // Verify that the seller object is not null
        assertNotNull(seller, "Seller object should not be null");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/objects"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("FIFA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("New Ultimate Team"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(seller.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userName").value("luka oniani"));
    }
}