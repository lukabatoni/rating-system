package com.lukaoniani.ratingsystems.repositories;

import com.lukaoniani.ratingsystems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);
}
