package com.lukaoniani.ratingsystems.controllers;

import com.lukaoniani.ratingsystems.dto.CommentRequestDto;
import com.lukaoniani.ratingsystems.dto.CommentResponseDto;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.services.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j  // Automatically adds the log object to the class
public class CommentController {

  private final CommentService commentService;

  // Create Comment
  @PostMapping
  public ResponseEntity<CommentResponseDto> createComment(
      @RequestBody CommentRequestDto commentRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    Integer authorId = null;  // Default to null for anonymous users

    if (userDetails instanceof User user) {
      authorId = user.getId();  // Set authorId if user is authenticated
    }

    log.debug("Request to create comment received. AuthorId: {}, CommentRequestDto: {}", authorId, commentRequestDto);

    CommentResponseDto createdComment = commentService.createComment(commentRequestDto, authorId);
    log.info("Comment created successfully. CommentId: {}", createdComment.getId());

    return ResponseEntity.ok(createdComment);
  }

  // Get Comments for Seller
  @GetMapping("/{sellerId}/comments")
  public ResponseEntity<List<CommentResponseDto>> getCommentsForSeller(
      @PathVariable Integer sellerId,
      @RequestParam(required = false) boolean approvedOnly) {

    log.debug("Fetching comments for seller with ID: {}. ApprovedOnly: {}", sellerId, approvedOnly);

    List<CommentResponseDto> comments = commentService.getCommentsBySeller(sellerId, approvedOnly);
    log.info("Fetched {} comments for seller with ID: {}", comments.size(), sellerId);

    return ResponseEntity.ok(comments);
  }

  // Get Comment by ID
  @GetMapping("/{sellerId}/comments/{commentId}")
  public ResponseEntity<CommentResponseDto> getCommentById(
      @PathVariable Integer sellerId,
      @PathVariable Integer commentId) {

    log.debug("Fetching comment with ID: {} for seller with ID: {}", commentId, sellerId);

    CommentResponseDto comment = commentService.getCommentById(commentId);
    log.info("Fetched comment with ID: {}", commentId);

    return ResponseEntity.ok(comment);
  }

  // Update Comment
  @PutMapping("/{sellerId}/comments/{commentId}")
  public ResponseEntity<CommentResponseDto> updateComment(
      @PathVariable Integer sellerId,
      @PathVariable Integer commentId,
      @RequestBody CommentRequestDto commentRequestDto,
      @AuthenticationPrincipal UserDetails userDetails) {

    log.debug("Request to update comment with ID: {}. SellerId: {}, CommentRequestDto: {}",
        commentId, sellerId, commentRequestDto);

    if (userDetails instanceof User user) {
      CommentResponseDto updatedComment = commentService.updateComment(commentId, commentRequestDto, user.getId());
      log.info("Comment with ID: {} updated successfully by user with ID: {}", commentId, user.getId());
      return ResponseEntity.ok(updatedComment);
    } else {
      log.error("Unauthorized access attempt to update comment with ID: {}", commentId);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update a comment");
    }
  }

  // Delete Comment
  @DeleteMapping("/{sellerId}/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable Integer sellerId,
      @PathVariable Integer commentId,
      @AuthenticationPrincipal UserDetails userDetails) {

    log.debug("Request to delete comment with ID: {} for seller with ID: {}", commentId, sellerId);

    if (userDetails instanceof User user) {
      commentService.deleteComment(commentId, user.getId());
      log.info("Comment with ID: {} deleted successfully by user with ID: {}", commentId, user.getId());
      return ResponseEntity.noContent().build();
    } else {
      log.error("Unauthorized access attempt to delete comment with ID: {}", commentId);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to delete a comment");
    }
  }
}
