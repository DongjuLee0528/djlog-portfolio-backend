package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Achievement 엔티티 데이터 액세스 인터페이스입니다.
 */
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
}
