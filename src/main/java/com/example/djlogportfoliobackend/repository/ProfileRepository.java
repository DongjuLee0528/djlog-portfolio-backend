package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 프로필 데이터 접근을 위한 Repository 인터페이스
 * 개인 프로필 정보에 대한 CRUD 작업을 제공합니다.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}