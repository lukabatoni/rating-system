package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.enums.Role;
import com.lukaoniani.rating_systems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndApproved(Role role, boolean approved);

    @Query("SELECT u FROM User u " +
            "WHERE u.role = 'SELLER' " +
            "AND EXISTS (SELECT g FROM GameObject g WHERE g.user.id = u.id AND g.title = :gameTitle) " +
            "AND (SELECT COALESCE(AVG(c.rating), 0.0) FROM Comment c WHERE c.seller.id = u.id AND c.approved = true) BETWEEN :minRating AND :maxRating " +
            "ORDER BY (SELECT COALESCE(AVG(c.rating), 0.0) FROM Comment c WHERE c.seller.id = u.id AND c.approved = true) DESC")
    List<User> findSellersByGameAndRating(
            @Param("gameTitle") String gameTitle,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating);
}

