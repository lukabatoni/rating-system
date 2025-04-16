package com.lukaoniani.ratingsystems.controllers;

import com.lukaoniani.ratingsystems.dto.SellerRequestDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.services.SellerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

  private final SellerService sellerService;

  // Get all Sellers (filtered by approval status)
  @GetMapping
  public ResponseEntity<List<SellerResponseDto>> getAllSellers(
      @RequestParam(defaultValue = "false") boolean approvedOnly) {

    log.debug("Request to get all sellers. Approved only: {}", approvedOnly);

    List<SellerResponseDto> sellers = sellerService.getAllSellers(approvedOnly);

    log.info("Retrieved {} sellers.", sellers.size());
    return ResponseEntity.ok(sellers);
  }

  // Get Seller Rating by Seller ID
  @GetMapping("/{sellerId}/rating")
  public ResponseEntity<Double> getSellerRating(@PathVariable Integer sellerId) {

    log.debug("Request to get rating for seller with ID: {}", sellerId);

    Double rating = sellerService.calculateSellerRating(sellerId);

    log.info("Seller with ID: {} has a rating of {}", sellerId, rating);
    return ResponseEntity.ok(rating);
  }

  // Get Top Sellers (with a limit on the number of top sellers)
  @GetMapping("/top")
  public ResponseEntity<List<SellerResponseDto>> getTopSellers(
      @RequestParam(defaultValue = "5") int limit) {

    log.debug("Request to get top {} sellers.", limit);

    List<SellerResponseDto> topSellers = sellerService.getTopSellers(limit);

    log.info("Retrieved {} top sellers.", topSellers.size());
    return ResponseEntity.ok(topSellers);
  }

  // Filter Sellers based on Game Title and Rating Range
  @GetMapping("/filter")
  public ResponseEntity<List<SellerResponseDto>> filterSellers(
      @RequestParam String gameTitle,
      @RequestParam Double minRating,
      @RequestParam Double maxRating) {

    log.debug("Request to filter sellers by gameTitle: {}, minRating: {}, maxRating: {}",
        gameTitle, minRating, maxRating);

    List<SellerResponseDto> filteredSellers =
        sellerService.filterSellersByGameAndRating(gameTitle, minRating, maxRating);

    log.info("Retrieved {} filtered sellers.", filteredSellers.size());
    return ResponseEntity.ok(filteredSellers);
  }

  // Create a new Seller
  @PostMapping
  public ResponseEntity<SellerResponseDto> createSeller(@RequestBody SellerRequestDto sellerRequestDto) {

    log.debug("Request to create a new seller with details: {}", sellerRequestDto);

    SellerResponseDto createdSeller = sellerService.createSeller(sellerRequestDto);

    log.info("Created new seller with ID: {}", createdSeller.getId());
    return ResponseEntity.ok(createdSeller);
  }
}
