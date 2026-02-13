package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findAllByOrderByPeriodDesc();
}