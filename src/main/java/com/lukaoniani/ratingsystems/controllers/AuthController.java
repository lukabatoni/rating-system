package com.lukaoniani.ratingsystems.controllers;

import com.lukaoniani.ratingsystems.dto.AuthenticationRequestDto;
import com.lukaoniani.ratingsystems.dto.AuthenticationResponseDto;
import com.lukaoniani.ratingsystems.dto.RegisterRequestDto;
import com.lukaoniani.ratingsystems.dto.ResetPasswordRequest;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import com.lukaoniani.ratingsystems.services.AuthService;
import com.lukaoniani.ratingsystems.services.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final RedisService redisService;
  private final UserRepository userRepository;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
    log.info("Attempting to register user with email: {}", request.getEmail());

    try {
      var response = authService.register(request);
      log.info("User registered successfully: {}", request.getEmail());
      return ResponseEntity.ok(response);
    } catch (Exception ex) {
      log.error("Registration failed for email {}: {}", request.getEmail(), ex.getMessage());
      throw ex;
    }
  }

  @GetMapping("/confirm")
  public ResponseEntity<String> confirmEmail(@RequestParam String email, @RequestParam String code) {
    log.info("Email confirmation attempt for: {}", email);

    String storedCode = redisService.getConfirmationCode(email);

    if (storedCode == null || !storedCode.equals(code)) {
      log.warn("Invalid or expired confirmation code for email: {}", email);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired confirmation code");
    }

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.warn("User not found during confirmation: {}", email);
          return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        });

    user.setEmailConfirmed(true);
    userRepository.save(user);
    redisService.deleteConfirmationCode(email);

    log.info("Email confirmed successfully: {}", email);
    return ResponseEntity.ok("Email confirmed successfully");
  }


  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
    log.info("Authentication attempt for email: {}", request.getEmail());

    try {
      var response = authService.authenticate(request);
      log.info("Authentication successful for: {}", request.getEmail());
      return ResponseEntity.ok(response);
    } catch (Exception ex) {
      log.warn("Authentication failed for {}: {}", request.getEmail(), ex.getMessage());
      throw ex;
    }
  }

  //email reset endpoints:

  @PostMapping("/forgot_password")
  public ResponseEntity<String> forgotPassword(@RequestParam String email) {
    log.info("Password reset requested for: {}", email);
    authService.sendPasswordResetCode(email);
    return ResponseEntity.ok("Password reset code sent to your email");
  }

  @PostMapping("/reset")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
    log.info("Password reset attempt with code: {}", request.getCode());
    authService.resetPassword(request.getCode(), request.getNewPassword());
    log.info("Password reset successful for code: {}", request.getCode());
    return ResponseEntity.ok("Password reset successfully");
  }

  @GetMapping("/check_code")
  public ResponseEntity<String> checkResetCode(@RequestParam String code) {
    log.debug("Checking validity of reset code: {}", code);
    boolean isValid = authService.isResetCodeValid(code);

    if (isValid) {
      log.info("Valid reset code: {}", code);
      return ResponseEntity.ok("Reset code is valid");
    } else {
      log.warn("Invalid or expired reset code: {}", code);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset code");
    }
  }
}
