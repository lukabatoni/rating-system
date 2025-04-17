package com.lukaoniani.ratingsystems.services;

import com.lukaoniani.ratingsystems.dto.CommentRequestDto;
import com.lukaoniani.ratingsystems.dto.CommentResponseDto;
import com.lukaoniani.ratingsystems.dto.SellerRequestDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.mappers.CommentMapper;
import com.lukaoniani.ratingsystems.models.Comment;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.CommentRepository;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
  // Error message constants
  private static final String ERROR_AUTHOR_NOT_FOUND = "Author not found";
  private static final String ERROR_SELLER_NOT_FOUND = "Seller not found";
  private static final String ERROR_MISSING_SELLER_INFO =
      "Seller info (firstName, lastName, email) is required when creating a new seller";
  private static final String ERROR_FAILED_TO_RETRIEVE_SELLER = "Failed to retrieve created seller";
  private static final String ERROR_COMMENT_NOT_FOUND = "Comment not found";
  private static final String ERROR_NOT_YOUR_COMMENT = "You can only update your own comments";
  private static final String ERROR_UNAUTHORIZED_DELETE =
      "You can only delete your own comments or comments on your account";

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final SellerService sellerService;
  private final CommentMapper commentMapper;

  public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer authorId) {
    // Check if the author exists, but allow for null authorId (anonymous user)
    User author = null;
    if (authorId != null) {
      author = userRepository.findById(authorId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_AUTHOR_NOT_FOUND));
    }

    // Find the seller (this is required)
    User seller;
    if (commentRequestDto.getSellerId() != null) {
      seller = userRepository.findById(commentRequestDto.getSellerId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_SELLER_NOT_FOUND));
    } else {
      // Create a new seller since sellerId is null
      if (commentRequestDto.getSellerEmail() == null
          || commentRequestDto.getSellerFirstName() == null
          || commentRequestDto.getSellerLastName() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_MISSING_SELLER_INFO);
      }

      SellerRequestDto sellerRequestDto = new SellerRequestDto(
          commentRequestDto.getSellerFirstName(),
          commentRequestDto.getSellerLastName(),
          commentRequestDto.getSellerEmail()
      );

      SellerResponseDto sellerResponseDto = sellerService.createSeller(sellerRequestDto);

      seller = userRepository.findById(sellerResponseDto.getId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
              ERROR_FAILED_TO_RETRIEVE_SELLER));
    }

    // Use mapper to convert DTO to entity
    Comment comment = commentMapper.toEntity(commentRequestDto);
    comment.setAuthor(author);  // null for anonymous users
    comment.setSeller(seller);

    comment = commentRepository.save(comment);

    return commentMapper.toDto(comment);
  }

  public List<CommentResponseDto> getCommentsBySeller(Integer sellerId, boolean approvedOnly) {
    List<Comment> comments;
    if (approvedOnly) {
      comments = commentRepository.findBySellerIdAndApproved(sellerId, true);
    } else {
      comments = commentRepository.findBySellerId(sellerId);
    }

    return comments.stream()
        .map(commentMapper::toDto)
        .collect(Collectors.toList());
  }

  public CommentResponseDto approveComment(Integer commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_COMMENT_NOT_FOUND));

    comment.setApproved(true);
    comment = commentRepository.save(comment);

    return commentMapper.toDto(comment);
  }

  public CommentResponseDto declineComment(Integer commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_COMMENT_NOT_FOUND));

    comment.setApproved(false);
    comment = commentRepository.save(comment);

    return commentMapper.toDto(comment);
  }

  public CommentResponseDto updateComment(Integer commentId, CommentRequestDto commentRequestDto, Integer authorId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_COMMENT_NOT_FOUND));

    if (!comment.getAuthor().getId().equals(authorId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR_NOT_YOUR_COMMENT);
    }

    // Use mapper to update entity from DTO
    commentMapper.updateCommentFromDto(commentRequestDto, comment);
    comment = commentRepository.save(comment);

    return commentMapper.toDto(comment);
  }

  public void deleteComment(Integer commentId, Integer userId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_COMMENT_NOT_FOUND));

    // Check if the user is either the comment author or the seller
    boolean isAuthor = comment.getAuthor() != null && comment.getAuthor().getId().equals(userId);
    boolean isSeller = comment.getSeller().getId().equals(userId);

    if (!isAuthor && !isSeller) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR_UNAUTHORIZED_DELETE);
    }

    commentRepository.delete(comment);
  }

  public CommentResponseDto getCommentById(Integer commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_COMMENT_NOT_FOUND));

    return commentMapper.toDto(comment);
  }
}