package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Column
    private String job;

    @Column
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column
    private String image;

    @Column
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    @Column
    private String github;

    @Column
    private String resume;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    public Profile(String name, String bio, String about, String image, String email, String github) {
        this.name = name;
        this.bio = bio;
        this.about = about;
        this.image = image;
        this.email = email;
        this.github = github;
    }
}