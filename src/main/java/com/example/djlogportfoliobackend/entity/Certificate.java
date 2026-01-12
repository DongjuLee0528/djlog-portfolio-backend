package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "certificate")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "자격증명은 필수입니다")
    private String name;

    @Column
    private String issuer;

    @Column
    private LocalDate issueDate;

    @Column
    private String credentialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Certificate(String name, String issuer, LocalDate issueDate, String credentialId, Profile profile) {
        this.name = name;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.credentialId = credentialId;
        this.profile = profile;
    }
}