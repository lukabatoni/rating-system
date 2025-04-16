package com.lukaoniani.ratingsystems.controllers;

import com.lukaoniani.ratingsystems.dto.CommentResponseDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.services.CommentService;
import com.lukaoniani.ratingsystems.services.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

  private final SellerService sellerService;
  private final CommentService commentService;

  @PutMapping("/approve-seller/{sellerId}")
  public ResponseEntity<SellerResponseDto> approveSeller(@PathVariable Integer sellerId) {
    log.debug("Request received to approve seller with ID: {}", sellerId);  // Log when request is received
    SellerResponseDto response = sellerService.approveSeller(sellerId);
    log.info("Seller with ID: {} has been approved", sellerId);  // Log when action is performed
    return ResponseEntity.ok(response);
  }

  @PutMapping("/approve-comment/{commentId}")
  public ResponseEntity<CommentResponseDto> approveComment(@PathVariable Integer commentId) {
    log.debug("Request received to approve comment with ID: {}", commentId);  // Log when request is received
    CommentResponseDto approvedComment = commentService.approveComment(commentId);
    log.info("Comment with ID: {} has been approved", commentId);  // Log when action is performed
    return ResponseEntity.ok(approvedComment);
  }

  // Decline Comment
  @PutMapping("/decline-comment/{commentId}")
  public ResponseEntity<CommentResponseDto> declineComment(@PathVariable Integer commentId) {
    log.debug("Request received to decline comment with ID: {}", commentId);  // Log when request is received
    CommentResponseDto declinedComment = commentService.declineComment(commentId);
    log.info("Comment with ID: {} has been declined", commentId);  // Log when action is performed
    return ResponseEntity.ok(declinedComment);
  }
}