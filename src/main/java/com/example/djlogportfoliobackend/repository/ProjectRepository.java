package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByStatusOrderByOrderAscTitleAsc(ProjectStatus status);

    List<Project> findAllByOrderByOrderAscTitleAsc();

    List<Project> findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(String category);

    List<Project> findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(String tag);
}