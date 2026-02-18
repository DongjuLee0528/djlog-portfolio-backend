package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Skill;
import lombok.Data;

import java.util.UUID;

/**
 * 기술 스택 정보 응답 DTO
 * 클라이언트에게 기술 스택 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
public class SkillResponse {
    private UUID id;
    private String name;
    private String category;
    private String proficiency;

    /**
     * Skill 엔티티로부터 응답 DTO를 생성하는 생성자
     * @param skill 변환할 Skill 엔티티
     */
    public SkillResponse(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.category = skill.getCategory();
        this.proficiency = skill.getProficiency();
    }
}