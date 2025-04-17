package com.lukaoniani.ratingsystems.services;

import com.lukaoniani.ratingsystems.dto.AuthenticationRequestDto;
import com.lukaoniani.ratingsystems.dto.AuthenticationResponseDto;
import com.lukaoniani.ratingsystems.dto.RegisterRequestDto;
import com.lukaoniani.ratingsystems.enums.Role;
import com.lukaoniani.ratingsystems.mappers.AuthMapper;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  // Constants for error messages
  private static final String ERROR_EMAIL_IN_USE = "Email already in use";
  private static final String ERROR_USER_NOT_FOUND = "User not found";
  private static final String ERROR_EMAIL_NOT_CONFIRMED = "Please confirm your email before logging in";
  private static final String ERROR_SELLER_NOT_APPROVED = "Seller not approved";
  private static final String ERROR_INVALID_RESET_CODE = "Invalid or expired reset code";

  // Base URL constant for links
  private static final String BASE_URL = "http://localhost:8080";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final RedisService redisService;
  private final AuthMapper authMapper;

  public AuthenticationResponseDto register(RegisterRequestDto request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_EMAIL_IN_USE);
    }

    // Use mapper to convert DTO to entity
    User user = authMapper.toEntity(request);

    // Set fields that need special handling
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.SELLER);
    user.setEmailConfirmed(false);
    user.setApproved(false);

    userRepository.save(user);

    // Generate a confirmation code
    String confirmationCode = UUID.randomUUID().toString();

    // Store the confirmation code in Redis
    redisService.saveConfirmationCode(request.getEmail(), confirmationCode);

    String confirmationLink = BASE_URL + "/api/auth/confirm?email=" + request.getEmail() + "&code=" + confirmationCode;
    System.out.println("Confirmation link: " + confirmationLink);

    var token = jwtService.generateToken(user);

    // Use mapper to create response
    return authMapper.toAuthResponseDto(token);
  }

  public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
    // Authenticate the user (throws an exception if authentication fails)
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_USER_NOT_FOUND));

    // Check if the email is confirmed
    if (!user.isEmailConfirmed()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ERROR_EMAIL_NOT_CONFIRMED);
    }

    // Check if the user is a seller and is approved
    if (user.getRole() == Role.SELLER && !user.isApproved()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ERROR_SELLER_NOT_APPROVED);
    }

    var token = jwtService.generateToken(user);

    // Use mapper to create response
    return authMapper.toAuthResponseDto(token);
  }

  public void sendPasswordResetCode(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_USER_NOT_FOUND));

    // Generate a reset code
    String resetCode = UUID.randomUUID().toString();

    // Store the reset code in Redis with a 24-hour expiration
    redisService.saveConfirmationCode(email, resetCode);

    // Simulate sending an email (log the reset link to the console)
    String resetLink = BASE_URL + "/api/auth/reset?code=" + resetCode;
    log.debug("Password reset link: {}", resetLink);
  }

  public void resetPassword(String code, String newPassword) {
    String email = redisService.getEmailByResetCode(code);
    if (email == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_INVALID_RESET_CODE);
    }

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_USER_NOT_FOUND));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    redisService.deleteConfirmationCode(email);
  }

  public boolean isResetCodeValid(String code) {
    // Check if the reset code exists in Redis
    String email = redisService.getEmailByResetCode(code);
    return email != null;
  }
}