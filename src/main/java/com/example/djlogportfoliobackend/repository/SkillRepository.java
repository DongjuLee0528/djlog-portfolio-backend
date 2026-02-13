package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    List<Skill> findAllByOrderByCategoryAscNameAsc();
}