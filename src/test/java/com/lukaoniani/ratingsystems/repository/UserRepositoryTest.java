package com.lukaoniani.ratingsystems.repository;

import com.lukaoniani.ratingsystems.enums.Role;
import com.lukaoniani.ratingsystems.models.User;
import com.lukaoniani.ratingsystems.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password");
        user1.setRole(Role.SELLER);
        entityManager.persist(user1);

        User user2 = new User();
        user2.setFirstName("Alice");
        user2.setLastName("Smith");
        user2.setEmail("alice.smith@example.com");
        user2.setPassword("password");
        user2.setRole(Role.ADMIN);
        entityManager.persist(user2);

        entityManager.flush();
    }

    @Test
    public void testFindByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testFindByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    public void testFindByRole() {
        List<User> sellers = userRepository.findByRole(Role.SELLER);
        assertThat(sellers).hasSize(1);
        assertThat(sellers.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.SELLER);

        User savedUser = userRepository.save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testDeleteUser() {
        User user = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        userRepository.delete(user);

        Optional<User> deletedUser = userRepository.findByEmail("john.doe@example.com");
        assertThat(deletedUser).isNotPresent();
    }
}
