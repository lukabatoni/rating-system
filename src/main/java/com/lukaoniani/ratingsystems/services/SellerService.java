package com.lukaoniani.ratingsystems.services;

import com.lukaoniani.ratingsystems.dto.SellerRequestDto;
import com.lukaoniani.ratingsystems.dto.SellerResponseDto;
import com.lukaoniani.ratingsystems.enums.Role;
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

  public SellerResponseDto createSeller(SellerRequestDto sellerRequestDto) {
    if (userRepository.findByEmail(sellerRequestDto.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
    }

    String tempPassword = UUID.randomUUID().toString().substring(0, 8);

    User seller = User.builder()
        .firstName(sellerRequestDto.getFirstName())
        .lastName(sellerRequestDto.getLastName())
        .email(sellerRequestDto.getEmail())
        .password(passwordEncoder.encode(tempPassword))
        .role(Role.SELLER)
        .approved(false)
        .emailConfirmed(false)
        .build();

    seller = userRepository.save(seller);

    return mapToDto(seller);
  }

  public SellerResponseDto approveSeller(Integer sellerId) {
    User seller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

    if (seller.getRole() != Role.SELLER) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a seller");
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
        .map(seller -> {

          // Calculate the average rating for the seller
          Double averageRating = commentRepository.getAverageRatingForSeller(seller.getId());

          // Count the number of approved comments for the seller
          int commentCount = commentRepository.countBySellerIdAndApproved(seller.getId(), true);

          // Map the seller to SellerResponseDto
          return mapToDto(seller);
        })
        //long
        .sorted((s1, s2) ->
            Double.compare(s2.getAverageRating(), s1.getAverageRating())) // Sort by average rating (descending)
        .limit(limit)
        .collect(Collectors.toList());
  }

  public List<SellerResponseDto> filterSellersByGameAndRating(String gameTitle, Double minRating, Double maxRating) {

    // Fetch sellers filtered by game title and rating range directly from the database
    List<User> sellers = userRepository.findSellersByGameAndRating(gameTitle, minRating, maxRating);

    return sellers.stream()
        .map(seller -> {

          // Calculate the average rating and comment count for the seller
          Double averageRating = commentRepository.getAverageRatingForSeller(seller.getId());
          int commentCount = commentRepository.countBySellerIdAndApproved(seller.getId(), true);

          // Map the seller to SellerResponseDto
          return mapToDto(seller);
        })
        .collect(Collectors.toList());
  }

  private SellerResponseDto mapToDto(User seller) {
    Double averageRating = commentRepository.getAverageRatingForSeller(seller.getId());

    int commentCount = commentRepository.countBySellerId(seller.getId());

    return SellerResponseDto.builder()
        .id(seller.getId())
        .firstName(seller.getFirstName())
        .lastName(seller.getLastName())
        .email(seller.getEmail())
        .createdAt(seller.getCreatedAt())
        .approved(seller.isApproved())
        .averageRating(averageRating != null ? averageRating : 0.0)
        .commentCount(commentCount)
        .build();
  }
}
