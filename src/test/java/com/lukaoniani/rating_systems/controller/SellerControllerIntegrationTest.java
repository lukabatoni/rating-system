package com.lukaoniani.rating_systems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukaoniani.rating_systems.controllers.SellerController;
import com.lukaoniani.rating_systems.dto.SellerRequestDto;
import com.lukaoniani.rating_systems.dto.SellerResponseDto;
import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import com.lukaoniani.rating_systems.services.SellerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Testcontainers
public class SellerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private SellerController sellerController;

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
        sellerController = new SellerController(sellerService);

        mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();

        userRepository.deleteAll();

        User seller = User.builder()
                .firstName("Luka")
                .lastName("Oniani")
                .email("lukaa.oniani2002@gmail.com")
                .password("password")
                .role(Role.SELLER)
                .approved(true)
                .emailConfirmed(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(seller);
    }

    @Test
    public void testGetSellerRating() throws Exception {
        Integer sellerId = 6;
        Double expectedRating = 8.333333333333334;

        // Mock the service method
        when(sellerService.calculateSellerRating(sellerId)).thenReturn(expectedRating);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers/{sellerId}/rating", sellerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedRating.toString()));
    }


    @Test
    @WithMockUser(username = "seller@example.com", roles = {"SELLER"})
    public void testCreateSeller() throws Exception {
        SellerRequestDto requestDto = new SellerRequestDto("Luka", "Oniani", "example@email.com");
        SellerResponseDto responseDto = new SellerResponseDto(1, "Luka", "Oniani", "example@email.com", null, false, 0.0, 0);

        when(sellerService.createSeller(requestDto)).thenReturn(responseDto);

        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertFalse(responseContent.isEmpty(), "Response content is empty");

        SellerResponseDto actualResponse = objectMapper.readValue(responseContent, SellerResponseDto.class);

        Assertions.assertEquals(1, actualResponse.getId());
        Assertions.assertEquals("Luka", actualResponse.getFirstName());
        Assertions.assertEquals("Oniani", actualResponse.getLastName());
        Assertions.assertEquals("example@email.com", actualResponse.getEmail());
    }

    @Test
    @WithMockUser(username = "seller@example.com", roles = {"SELLER"})
    public void testGetAllSellers() throws Exception {
        List<SellerResponseDto> sellers = Arrays.asList(
                new SellerResponseDto(1, "Seller1", "LastName1", "email1@example.com", null, false, 4.5, 0),
                new SellerResponseDto(2, "Seller2", "LastName2", "email2@example.com", null, false, 4.7, 0)
        );

        when(sellerService.getAllSellers(false)).thenReturn(sellers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers")
                        .param("approvedOnly", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Seller1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("LastName1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("email1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].averageRating").value(4.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Seller2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value("LastName2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("email2@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].averageRating").value(4.7));
    }
}