package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
