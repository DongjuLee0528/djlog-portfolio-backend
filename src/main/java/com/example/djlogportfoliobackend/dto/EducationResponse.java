package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Education;
import lombok.Data;

import java.util.UUID;

/**
 * 학력 정보 응답 DTO
 * 클라이언트에게 학력 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
public class EducationResponse {
    private UUID id;
    private String school;
    private String major;
    private String period;
    private String degree;

    /**
     * Education 엔티티로부터 응답 DTO를 생성하는 생성자
     * @param education 변환할 Education 엔티티
     */
    public EducationResponse(Education education) {
        this.id = education.getId();
        this.school = education.getSchool();
        this.major = education.getMajor();
        this.period = education.getPeriod();
        this.degree = education.getDegree();
    }
}