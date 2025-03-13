package com.lukaoniani.rating_systems.repositories;

import com.lukaoniani.rating_systems.models.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
    List<GameObject> findByUserId(Integer userId);
}
