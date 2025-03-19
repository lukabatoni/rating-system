package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.CommentResponseDto;
import com.lukaoniani.rating_systems.dto.SellerResponseDto;
import com.lukaoniani.rating_systems.services.CommentService;
import com.lukaoniani.rating_systems.services.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SellerService sellerService;
    private final CommentService commentService;

    @PutMapping("/approve-seller/{sellerId}")
    public ResponseEntity<SellerResponseDto> approveSeller(@PathVariable Integer sellerId) {
        return ResponseEntity.ok(sellerService.approveSeller(sellerId));
    }

    @PutMapping("/approve-comment/{commentId}")
    public ResponseEntity<CommentResponseDto> approveComment(@PathVariable Integer commentId) {

        CommentResponseDto approvedComment = commentService.approveComment(commentId);
        return ResponseEntity.ok(approvedComment);
    }

    @PutMapping("/decline-comment/{commentId}")
    public ResponseEntity<CommentResponseDto> declineComment(@PathVariable Integer commentId) {

        CommentResponseDto declineComment = commentService.declineComment(commentId);
        return ResponseEntity.ok(declineComment);
    }
}