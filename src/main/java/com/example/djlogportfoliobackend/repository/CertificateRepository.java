package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findAllByOrderByIssueDateDesc();
}