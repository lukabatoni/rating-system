package com.lukaoniani.rating_systems.services;

import com.lukaoniani.rating_systems.dto.AuthenticationRequestDto;
import com.lukaoniani.rating_systems.dto.AuthenticationResponseDto;
import com.lukaoniani.rating_systems.dto.RegisterRequestDto;
import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    //private final EmailService emailService;
    //private final RedisTemplate<String, String> redisTemplate;

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

        // Generate and send verification code
       // String verificationCode = UUID.randomUUID().toString().substring(0, 6);
       // redisTemplate.opsForValue().set("verification:" + user.getEmail(), verificationCode, 24, TimeUnit.HOURS);
       // emailService.sendVerificationCode(user.getEmail(), verificationCode);

        var token = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isEmailConfirmed()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not confirmed");
        }

        if (user.getRole() == Role.SELLER && !user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Seller not approved");
        }

        var token = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

//    public void verifyEmail(String email, String code) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
////        String storedCode = redisTemplate.opsForValue().get("verification:" + email);
////
////        if (storedCode == null || !storedCode.equals(code)) {
////            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code");
////        }
//
//        user.setEmailConfirmed(true);
//        userRepository.save(user);
//
//        // Delete the code from Redis
////        redisTemplate.delete("verification:" + email);
//    }

//    public void forgotPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        String resetCode = UUID.randomUUID().toString().substring(0, 8);
//        redisTemplate.opsForValue().set("reset:" + email, resetCode, 24, TimeUnit.HOURS);
//
//        emailService.sendVerificationCode(email, resetCode);
//    }

//    public void resetPassword(String email, String code, String newPassword) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        String storedCode = redisTemplate.opsForValue().get("reset:" + email);
//
//        if (storedCode == null || !storedCode.equals(code)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reset code");
//        }
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        // Delete the code from Redis
//        redisTemplate.delete("reset:" + email);
//    }

//    public boolean checkResetCode(String email, String code) {
//        String storedCode = redisTemplate.opsForValue().get("reset:" + email);
//        return storedCode != null && storedCode.equals(code);
//    }
}
