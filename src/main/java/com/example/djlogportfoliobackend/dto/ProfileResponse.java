package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProfileResponse {

    private UUID id;
    private String name;
    private String bio;
    private String about;
    private String image;
    private String email;
    private String github;
}