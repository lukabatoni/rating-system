package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.models.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
    List<GameObject> findByUserId(Integer userId);
}
