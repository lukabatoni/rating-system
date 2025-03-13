package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findBySellerId(Integer sellerId);
    List<Comment> findBySellerIdAndApproved(Integer sellerId, boolean approved);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.seller.id = ?1 AND c.approved = true")
    Double getAverageRatingForSeller(Integer sellerId);
}
