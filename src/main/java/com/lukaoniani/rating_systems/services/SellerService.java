package com.lukaoniani.rating_systems.services;

import com.lukaoniani.rating_systems.dto.SellerRequestDto;
import com.lukaoniani.rating_systems.dto.SellerResponseDto;
import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.CommentRepository;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
//    private final EmailService emailService;

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

        // Send temporary password to seller's email
//        emailService.sendTempPassword(seller.getEmail(), tempPassword);


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

        // Notify seller about approval
//        emailService.sendApprovalNotification(seller.getEmail());
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

    public List<SellerResponseDto> getTopSellers(int limit) {
        List<User> sellers = userRepository.findByRoleAndApproved(Role.SELLER, true);

        return sellers.stream()
                .map(this::mapToDto)
                .sorted((s1, s2) -> Double.compare(
                        s2.getAverageRating() != null ? s2.getAverageRating() : 0.0,
                        s1.getAverageRating() != null ? s1.getAverageRating() : 0.0))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private SellerResponseDto mapToDto(User seller) {
        Double averageRating = commentRepository.getAverageRatingForSeller(seller.getId());

        // Instead of using seller.getComments(), use the commentRepository to get the count
        int commentCount = commentRepository.countBySellerId(seller.getId());

        return SellerResponseDto.builder()
                .id(seller.getId())
                .firstName(seller.getFirstName())
                .lastName(seller.getLastName())
                .email(seller.getEmail())
                .createdAt(seller.getCreatedAt())
                .approved(seller.isApproved())
                .averageRating(averageRating)
                .commentCount(commentCount)
                .build();
    }
}
