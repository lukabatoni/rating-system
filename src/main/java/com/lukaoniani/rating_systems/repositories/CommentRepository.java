package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findBySellerId(Integer sellerId);
    List<Comment> findBySellerIdAndApproved(Integer sellerId, boolean approved);

    int countBySellerId(Integer sellerId);

    int countBySellerIdAndApproved(Integer sellerId, boolean approved);
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.seller.id = ?1 AND c.approved = true")
    Double getAverageRatingForSeller(Integer sellerId);
}
