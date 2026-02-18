package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.dto.SkillResponse;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.entity.Skill;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import com.example.djlogportfoliobackend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 기술 스택 정보 관리 서비스
 * 기술 스택 정보의 CRUD 작업을 처리하고 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 기술 스택 정보 조회
     * 카테고리와 이름 순으로 오름차순 정렬하여 반환합니다.
     * @return 기술 스택 정보 리스트
     */
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(SkillResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 기술 스택 정보 등록
     * @param request 기술 스택 정보 등록 요청 DTO
     * @return 등록된 기술 스택 정보 응답 DTO
     * @throws RuntimeException 프로필이 존재하지 않을 때
     */
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("프로필이 존재하지 않습니다."));

        Skill skill = new Skill(
                request.getName(),
                request.getCategory(),
                request.getProficiency(),
                profile
        );

        Skill savedSkill = skillRepository.save(skill);
        return new SkillResponse(savedSkill);
    }

    /**
     * 기술 스택 정보 수정
     * @param id 수정할 기술 스택 정보 ID
     * @param request 기술 스택 정보 수정 요청 DTO
     * @return 수정된 기술 스택 정보 응답 DTO
     * @throws RuntimeException 기술 스택 정보를 찾을 수 없을 때
     */
    @Transactional
    public SkillResponse updateSkill(UUID id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기술 스택 정보를 찾을 수 없습니다."));

        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setProficiency(request.getProficiency());

        return new SkillResponse(skill);
    }

    /**
     * 기술 스택 정보 삭제
     * @param id 삭제할 기술 스택 정보 ID
     * @throws RuntimeException 기술 스택 정보를 찾을 수 없을 때
     */
    @Transactional
    public void deleteSkill(UUID id) {
        if (!skillRepository.existsById(id)) {
            throw new RuntimeException("기술 스택 정보를 찾을 수 없습니다.");
        }
        skillRepository.deleteById(id);
    }
}