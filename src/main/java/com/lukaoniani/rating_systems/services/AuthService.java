package com.lukaoniani.rating_systems.services;

import com.lukaoniani.rating_systems.dto.AuthenticationRequestDto;
import com.lukaoniani.rating_systems.dto.AuthenticationResponseDto;
import com.lukaoniani.rating_systems.dto.RegisterRequestDto;
import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    public AuthenticationResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SELLER)
                .emailConfirmed(false)
                .approved(false)
                .build();

        userRepository.save(user);

        // Generate a confirmation code
        String confirmationCode = UUID.randomUUID().toString();

        // Store the confirmation code in Redis
        redisService.saveConfirmationCode(request.getEmail(), confirmationCode);

        String confirmationLink = "http://localhost:8080/api/auth/confirm?email=" + request.getEmail() + "&code=" + confirmationCode;
        System.out.println("Confirmation link: " + confirmationLink);

        var token = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if the email is confirmed
        if (!user.isEmailConfirmed()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please confirm your email before logging in");
        }

        // Check if the user is a seller and is approved
        if (user.getRole() == Role.SELLER && !user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Seller not approved");
        }

        var token = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

    public void sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Generate a reset code
        String resetCode = UUID.randomUUID().toString();

        // Store the reset code in Redis with a 24-hour expiration
        redisService.saveConfirmationCode(email, resetCode);

        // Simulate sending an email (log the reset link to the console)
        String resetLink = "http://localhost:8080/api/auth/reset?code=" + resetCode;
        System.out.println("Password reset link: " + resetLink);
    }

    public void resetPassword(String code, String newPassword) {
        String email = redisService.getEmailByResetCode(code);
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
