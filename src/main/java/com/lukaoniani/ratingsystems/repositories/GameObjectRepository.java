package com.lukaoniani.ratingsystems.repositories;

import com.lukaoniani.ratingsystems.models.GameObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
  List<GameObject> findByUserId(Integer userId);
}
