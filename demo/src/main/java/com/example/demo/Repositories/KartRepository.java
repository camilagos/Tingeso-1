package com.example.demo.Repositories;

import com.example.demo.Entities.KartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {
    List<KartEntity> findByAvailable (boolean available);
}
