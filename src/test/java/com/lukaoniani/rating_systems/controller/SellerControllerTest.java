package com.lukaoniani.rating_systems.controller;

import com.lukaoniani.rating_systems.controllers.SellerController;
import com.lukaoniani.rating_systems.services.SellerService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Enable Mockito in the test
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SellerService sellerService; // Use Mockito's @Mock

    @InjectMocks
    private SellerController sellerController; // Controller instance

    @BeforeEach
    public void setUp() {
        // Manually create an instance of SellerController and inject the mock service
        sellerController = new SellerController(sellerService);

        // Initialize MockMvc with the standalone controller
        mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();
    }

    @Test
    @WithMockUser(username = "seller@example.com", roles = {"SELLER"}) // Simulate an authenticated seller
    public void testGetSellerRating() throws Exception {
        // Arrange
        Integer sellerId = 6;
        Double expectedRating = 8.333333333333334;

        // Mock the service method
        Mockito.when(sellerService.calculateSellerRating(sellerId)).thenReturn(expectedRating);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers/{sellerId}/rating", sellerId))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Verify HTTP 200 status
                .andExpect(MockMvcResultMatchers.content().string(expectedRating.toString())); // Verify response body
    }

    @Test
    public void testGetSellerRating_Unauthenticated() throws Exception {
        // Arrange
        Integer sellerId = 6;

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers/{sellerId}/rating", sellerId))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // Verify HTTP 401 status
    }
}

