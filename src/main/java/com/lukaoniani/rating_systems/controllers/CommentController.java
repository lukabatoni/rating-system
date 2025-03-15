package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.CommentRequestDto;
import com.lukaoniani.rating_systems.dto.CommentResponseDto;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.services.CommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

//    @GetMapping("/seller/{sellerId}")
//    public ResponseEntity<List<CommentResponseDto>> getSellerComments(
//            @PathVariable Integer sellerId,
//            @RequestParam(defaultValue = "true") boolean approvedOnly) {
//        return ResponseEntity.ok(commentService.getCommentsBySeller(sellerId, approvedOnly));
//    }

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


//    @PutMapping("/{commentId}/approve")
//    public ResponseEntity<CommentResponseDto> approveComment(@PathVariable Integer commentId) {
//        return ResponseEntity.ok(commentService.approveComment(commentId));
//    }
//
//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<Void> deleteComment(
//            @PathVariable Integer commentId,
//            @AuthenticationPrincipal User user) {
//        commentService.deleteComment(commentId, user.getId());
//        return ResponseEntity.noContent().build();
//    }
}
