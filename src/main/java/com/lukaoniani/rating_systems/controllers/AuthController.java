package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.AuthenticationRequestDto;
import com.lukaoniani.rating_systems.dto.AuthenticationResponseDto;
import com.lukaoniani.rating_systems.dto.RegisterRequestDto;
import com.lukaoniani.rating_systems.dto.ResetPasswordRequest;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import com.lukaoniani.rating_systems.services.AuthService;
import com.lukaoniani.rating_systems.services.RedisService;
import com.lukaoniani.rating_systems.services.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @RequestParam String email,
            @RequestParam String code) {
        // Retrieve the confirmation code from Redis
        String storedCode = redisService.getConfirmationCode(email);

        if (storedCode == null || !storedCode.equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired confirmation code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setEmailConfirmed(true);
        userRepository.save(user);

        redisService.deleteConfirmationCode(email);

        return ResponseEntity.ok("Email confirmed successfully");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    //email reset endpoints:

    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetCode(email);
        return ResponseEntity.ok("Password reset code sent to your email");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getCode(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping("/check_code")
    public ResponseEntity<String> checkResetCode(@RequestParam String code) {
        boolean isValid = authService.isResetCodeValid(code);
        if (isValid) {
            return ResponseEntity.ok("Reset code is valid");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset code");
        }

    }
}
