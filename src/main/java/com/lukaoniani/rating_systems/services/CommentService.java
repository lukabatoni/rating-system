package com.lukaoniani.rating_systems.services;

import com.lukaoniani.rating_systems.dto.CommentRequestDto;
import com.lukaoniani.rating_systems.dto.CommentResponseDto;
import com.lukaoniani.rating_systems.dto.SellerRequestDto;
import com.lukaoniani.rating_systems.dto.SellerResponseDto;
import com.lukaoniani.rating_systems.models.Comment;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.CommentRepository;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SellerService sellerService;

    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer authorId) {
        // Check if the author exists, but allow for null authorId (anonymous user)
        User author = null;
        if (authorId != null) {
            author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        }

        // Find the seller (this is required)
        User seller;
        if (commentRequestDto.getSellerId() != null) {
            // Try to find existing seller
            seller = userRepository.findById(commentRequestDto.getSellerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        } else {
            // Create a new seller since sellerId is null
            // Check required fields for new seller
            if (commentRequestDto.getSellerEmail() == null ||
                    commentRequestDto.getSellerFirstName() == null ||
                    commentRequestDto.getSellerLastName() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Seller info (firstName, lastName, email) is required when creating a new seller");
            }

            SellerRequestDto sellerRequestDto = new SellerRequestDto(
                    commentRequestDto.getSellerFirstName(),
                    commentRequestDto.getSellerLastName(),
                    commentRequestDto.getSellerEmail()
            );

            // Get the DTO from service
            SellerResponseDto sellerResponseDto = sellerService.createSeller(sellerRequestDto);

            // Fetch the actual entity using the ID from the response
            seller = userRepository.findById(sellerResponseDto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to retrieve created seller"));
        }

        // Create the comment object, allowing for null author (anonymous comment)
        Comment comment = Comment.builder()
                .message(commentRequestDto.getMessage())
                .rating(commentRequestDto.getRating())
                .author(author)  // null for anonymous users
                .seller(seller)
                .approved(false)
                .build();

        comment = commentRepository.save(comment);

        return mapToDto(comment);
    }

    public List<CommentResponseDto> getCommentsBySeller(Integer sellerId, boolean approvedOnly) {
        List<Comment> comments;
        if (approvedOnly) {
            comments = commentRepository.findBySellerIdAndApproved(sellerId, true);
        } else {
            comments = commentRepository.findBySellerId(sellerId);
        }

        return comments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CommentResponseDto approveComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        comment.setApproved(true);
        comment = commentRepository.save(comment);

        return mapToDto(comment);
    }

    public CommentResponseDto declineComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        comment.setApproved(false);
        comment = commentRepository.save(comment);

        return mapToDto(comment);
    }

    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDto mapToDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .rating(comment.getRating())
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .authorName(comment.getAuthor() != null
                        ? comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName()
                        : "Anonymous")
                .createdAt(comment.getCreatedAt())
                .approved(comment.isApproved())
                .build();
    }

}
