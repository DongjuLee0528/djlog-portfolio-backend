package com.example.djlogportfoliobackend.entity;

public enum ProjectStatus {
    DRAFT("Draft"),
    PUBLISHED("Published");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}