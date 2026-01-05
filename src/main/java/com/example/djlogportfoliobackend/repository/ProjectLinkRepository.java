package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectLinkRepository extends JpaRepository<ProjectLink, UUID> {

    List<ProjectLink> findByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);
}