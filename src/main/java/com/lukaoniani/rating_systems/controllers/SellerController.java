package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.SellerRequestDto;
import com.lukaoniani.rating_systems.dto.SellerResponseDto;
import com.lukaoniani.rating_systems.services.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<SellerResponseDto>> getAllSellers(
            @RequestParam(defaultValue = "false") boolean approvedOnly) {
        return ResponseEntity.ok(sellerService.getAllSellers(approvedOnly));
    }

    @GetMapping("/{sellerId}/rating")
    public ResponseEntity<Double> getSellerRating(@PathVariable Integer sellerId) {
        Double rating = sellerService.calculateSellerRating(sellerId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/top")
    public ResponseEntity<List<SellerResponseDto>> getTopSellers(
            @RequestParam(defaultValue = "5") int limit) {
        List<SellerResponseDto> topSellers = sellerService.getTopSellers(limit);
        return ResponseEntity.ok(topSellers);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SellerResponseDto>> filterSellers(
            @RequestParam String gameTitle,
            @RequestParam Double minRating,
            @RequestParam Double maxRating) {
        List<SellerResponseDto> filteredSellers = sellerService.filterSellersByGameAndRating(gameTitle, minRating, maxRating);
        return ResponseEntity.ok(filteredSellers);
    }

    @PostMapping
    public ResponseEntity<SellerResponseDto> createSeller(@RequestBody SellerRequestDto sellerRequestDto) {
        return ResponseEntity.ok(sellerService.createSeller(sellerRequestDto));
    }

//    @PutMapping("/{sellerId}/approve")
//    public ResponseEntity<SellerResponseDto> approveSeller(@PathVariable Integer sellerId) {
//        return ResponseEntity.ok(sellerService.approveSeller(sellerId));
//    }

}
