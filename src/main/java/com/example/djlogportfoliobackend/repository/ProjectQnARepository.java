package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectQnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectQnARepository extends JpaRepository<ProjectQnA, UUID> {

    List<ProjectQnA> findByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);
}