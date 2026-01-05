package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    private String bio;

    private String about;

    private String image;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    private String github;
}