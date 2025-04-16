package com.lukaoniani.ratingsystems.config;

import com.lukaoniani.ratingsystems.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up the security-related beans in the Spring application context.
 * This includes authentication services, password encoding, and user details service.
 * The class uses Spring Security to configure authentication for the application.
 * The configuration includes:
 * <ul>
 *   <li>UserDetailsService for loading user-specific data.</li>
 *   <li>AuthenticationProvider for handling authentication logic.</li>
 *   <li>AuthenticationManager for managing authentication requests.</li>
 *   <li>PasswordEncoder to securely encode passwords.</li>
 * </ul>
 */
@Configuration
@AllArgsConstructor
public class ApplicationConfig {

  /**
   * UserRepository instance for accessing user data from the database.
   */
  private final UserRepository repository;

  /**
   * Bean for the UserDetailsService used by Spring Security to load user-specific data.
   *
   * @return a UserDetailsService that retrieves a user by their email.
   * @throws UsernameNotFoundException if the user cannot be found.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username ->
        repository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  /**
   * Bean for AuthenticationProvider that handles authentication using a DaoAuthenticationProvider.
   * It uses the UserDetailsService and PasswordEncoder beans for authentication.
   *
   * @return a configured AuthenticationProvider.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Bean for AuthenticationManager to manage authentication requests.
   * This manager is required for performing authentication operations in Spring Security.
   *
   * @param config the AuthenticationConfiguration to provide an AuthenticationManager.
   * @return the configured AuthenticationManager.
   * @throws Exception if an error occurs while getting the authentication manager.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Bean for PasswordEncoder used to encode passwords in a secure manner.
   * This uses the BCryptPasswordEncoder for hashing passwords.
   *
   * @return a configured PasswordEncoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
