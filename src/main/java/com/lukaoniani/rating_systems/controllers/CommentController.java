package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.CommentRequestDto;
import com.lukaoniani.rating_systems.dto.CommentResponseDto;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.services.CommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer authorId = null;  // Default to null for anonymous users

        if (userDetails instanceof User user) {
            authorId = user.getId();  // Set authorId if user is authenticated
        }

        return ResponseEntity.ok(commentService.createComment(commentRequestDto, authorId));
    }

    //get all comments of user
    @GetMapping("/{sellerId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForSeller(
            @PathVariable Integer sellerId,
            @RequestParam(required = false) boolean approvedOnly) {

        List<CommentResponseDto> comments = commentService.getCommentsBySeller(sellerId, approvedOnly);
        return ResponseEntity.ok(comments);
    }

    //get certain comment of user
    @GetMapping("/{sellerId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(
            @PathVariable Integer sellerId,
            @PathVariable Integer commentId) {

        CommentResponseDto comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{sellerId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Integer sellerId,
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof User user) {
            CommentResponseDto updatedComment = commentService.updateComment(commentId, commentRequestDto, user.getId());
            return ResponseEntity.ok(updatedComment);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update a comment");
        }
    }

    @DeleteMapping("/{sellerId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer sellerId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof User user) {
            commentService.deleteComment(commentId, user.getId());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to delete a comment");
        }

    }


}
