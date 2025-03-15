package com.lukaoniani.rating_systems.config;

import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import com.lukaoniani.rating_systems.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DatabaseSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            // Check if an admin exists already in the database
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.ADMIN)
                        .approved(true)
                        .emailConfirmed(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                userRepository.save(admin);
                System.out.println("Admin user seeded successfully!");
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
