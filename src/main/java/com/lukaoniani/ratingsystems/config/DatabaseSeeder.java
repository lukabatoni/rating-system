package com.lukaoniani.ratingsystems.config;

import com.lukaoniani.ratingsystems.enums.Role;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The DatabaseSeeder class is responsible for seeding the database with an admin user if one does not already exist.
 * This class uses CommandLineRunner to run a task at the application startup to check if the admin user is present.
 * If the admin user is missing, it will create one with predefined details (username, password, role, etc.)
 * and save it to the database.
 * The admin user created by this class has the following default properties:
 * <ul>
 *   <li>Email: admin@example.com</li>
 *   <li>Password: password (encoded using BCrypt)</li>
 *   <li>Role: ADMIN</li>
 *   <li>Approved: true</li>
 *   <li>Email Confirmed: true</li>
 *   <li>Created At: current timestamp</li>
 * </ul>
 */
@Component
@AllArgsConstructor
public class DatabaseSeeder {

  /**
   * The UserRepository is used to interact with the database for user-related operations.
   */
  private final UserRepository userRepository;

  /**
   * The PasswordEncoder is used to securely encode the password before saving it to the database.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Bean method to seed the database at application startup.
   * It checks whether an admin user exists in the database. If not, it creates and saves a new admin user.
   * This method is executed when the application starts.
   *
   * @return a CommandLineRunner that will seed the database if necessary.
   */
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
