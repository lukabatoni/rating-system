package com.lukaoniani.rating_systems.controllers;

import com.lukaoniani.rating_systems.dto.AuthenticationRequestDto;
import com.lukaoniani.rating_systems.dto.AuthenticationResponseDto;
import com.lukaoniani.rating_systems.dto.RegisterRequestDto;
import com.lukaoniani.rating_systems.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

//    @PostMapping("/verify-email")
//    public ResponseEntity<Map<String, String>> verifyEmail(
//            @RequestParam String email,
//            @RequestParam String code) {
//        authService.verifyEmail(email, code);
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Email verified successfully");
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
//        authService.forgotPassword(email);
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Reset code sent to email");
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<Map<String, String>> resetPassword(
//            @RequestParam String email,
//            @RequestParam String code,
//            @RequestParam String newPassword) {
//        authService.resetPassword(email, code, newPassword);
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Password reset successfully");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/check-code")
//    public ResponseEntity<Map<String, Boolean>> checkResetCode(
//            @RequestParam String email,
//            @RequestParam String code) {
//        boolean isValid = authService.checkResetCode(email, code);
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("valid", isValid);
//        return ResponseEntity.ok(response);


//    }
}
