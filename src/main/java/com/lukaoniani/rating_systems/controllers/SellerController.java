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

    @PostMapping
    public ResponseEntity<SellerResponseDto> createSeller(@RequestBody SellerRequestDto sellerRequestDto) {
        return ResponseEntity.ok(sellerService.createSeller(sellerRequestDto));
    }

    @PutMapping("/{sellerId}/approve")
    public ResponseEntity<SellerResponseDto> approveSeller(@PathVariable Integer sellerId) {
        return ResponseEntity.ok(sellerService.approveSeller(sellerId));
    }

    @GetMapping
    public ResponseEntity<List<SellerResponseDto>> getAllSellers(
            @RequestParam(defaultValue = "false") boolean approvedOnly) {
        return ResponseEntity.ok(sellerService.getAllSellers(approvedOnly));
    }

    @GetMapping("/top")
    public ResponseEntity<List<SellerResponseDto>> getTopSellers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(sellerService.getTopSellers(limit));
    }
}
