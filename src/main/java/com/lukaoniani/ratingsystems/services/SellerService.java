package com.lukaoniani.ratingsystems.services;

import com.lukaoniani.ratingsystems.dto.SellerRequestDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.enums.Role;
import com.lukaoniani.ratingsystems.mappers.SellerMapper;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.CommentRepository;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SellerService {
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;
  private final SellerMapper sellerMapper;

  // Error message constants
  private static final String ERROR_EMAIL_IN_USE = "Email already in use";
  private static final String ERROR_SELLER_NOT_FOUND = "Seller not found";
  private static final String ERROR_NOT_SELLER = "User is not a seller";

  public SellerResponseDto createSeller(SellerRequestDto sellerRequestDto) {
    if (userRepository.findByEmail(sellerRequestDto.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_EMAIL_IN_USE);
    }

    String tempPassword = UUID.randomUUID().toString().substring(0, 8);

    // Convert DTO to entity with MapStruct, then set additional fields
    User seller = sellerMapper.toEntity(sellerRequestDto);
    seller.setPassword(passwordEncoder.encode(tempPassword));
    seller.setRole(Role.SELLER);
    seller.setApproved(false);
    seller.setEmailConfirmed(false);

    seller = userRepository.save(seller);

    return mapToDto(seller);
  }

  public SellerResponseDto approveSeller(Integer sellerId) {
    User seller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_SELLER_NOT_FOUND));

    if (seller.getRole() != Role.SELLER) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_NOT_SELLER);
    }

    seller.setApproved(true);
    seller = userRepository.save(seller);

    return mapToDto(seller);
  }

  public List<SellerResponseDto> getAllSellers(boolean approvedOnly) {
    List<User> sellers;
    if (approvedOnly) {
      sellers = userRepository.findByRoleAndApproved(Role.SELLER, true);
    } else {
      sellers = userRepository.findByRole(Role.SELLER);
    }

    return sellers.stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  public Double calculateSellerRating(Integer sellerId) {
    return commentRepository.getAverageRatingForSeller(sellerId);
  }

  public List<SellerResponseDto> getTopSellers(int limit) {
    // Fetch all sellers with the SELLER role
    List<User> sellers = userRepository.findByRole(Role.SELLER);

    return sellers.stream()
        .map(this::mapToDto)
        .sorted((s1, s2) ->
            Double.compare(s2.getAverageRating(), s1.getAverageRating())) // Sort by average rating (descending)
        .limit(limit)
        .collect(Collectors.toList());
  }

  public List<SellerResponseDto> filterSellersByGameAndRating(String gameTitle, Double minRating, Double maxRating) {
    // Fetch sellers filtered by game title and rating range directly from the database
    List<User> sellers = userRepository.findSellersByGameAndRating(gameTitle, minRating, maxRating);

    return sellers.stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  private SellerResponseDto mapToDto(User seller) {
    Double averageRating = commentRepository.getAverageRatingForSeller(seller.getId());
    int commentCount = commentRepository.countBySellerId(seller.getId());

    // Use MapStruct for the basic mapping
    SellerResponseDto dto = sellerMapper.toDto(seller);

    // Use @Context parameters for the calculated fields
   // sellerMapper.setRatingAndCommentCount(dto, averageRating, commentCount);

    return dto;
  }
}