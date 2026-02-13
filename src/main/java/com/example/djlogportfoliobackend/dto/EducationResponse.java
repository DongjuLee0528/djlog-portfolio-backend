package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Education;
import lombok.Data;

import java.util.UUID;

@Data
public class EducationResponse {
    private UUID id;
    private String school;
    private String major;
    private String period;
    private String degree;

    public EducationResponse(Education education) {
        this.id = education.getId();
        this.school = education.getSchool();
        this.major = education.getMajor();
        this.period = education.getPeriod();
        this.degree = education.getDegree();
    }
}