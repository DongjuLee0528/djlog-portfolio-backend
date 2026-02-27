package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.dto.SkillResponse;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.entity.Skill;
import com.example.djlogportfoliobackend.exception.ResourceNotFoundException;
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
 * 기술 스택 데이터의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 기술 스택 목록 조회
     * 카테고리와 이름 순으로 오름차순 정렬하여 반환합니다.
     *
     * @return 기술 스택 목록
     */
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 기술 스택 정보 생성
     *
     * @param request 기술 스택 생성 요청 데이터
     * @return 생성된 기술 스택 정보
     * @throws ResourceNotFoundException 프로필이 존재하지 않을 경우
     */
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("프로필이 존재하지 않습니다."));

        Skill skill = new Skill(
                request.getName(),
                request.getCategory(),
                request.getProficiency(),
                profile
        );

        Skill savedSkill = skillRepository.save(skill);
        return toResponse(savedSkill);
    }

    /**
     * 기존 기술 스택 정보 수정
     *
     * @param id 수정할 기술 스택 ID
     * @param request 수정할 기술 스택 데이터
     * @return 수정된 기술 스택 정보
     * @throws ResourceNotFoundException 기술 스택 정보를 찾을 수 없을 경우
     */
    @Transactional
    public SkillResponse updateSkill(UUID id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("기술 스택 정보를 찾을 수 없습니다."));

        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setProficiency(request.getProficiency());

        return toResponse(skill);
    }

    /**
     * 기술 스택 정보 삭제
     *
     * @param id 삭제할 기술 스택 ID
     * @throws ResourceNotFoundException 기술 스택 정보를 찾을 수 없을 경우
     */
    @Transactional
    public void deleteSkill(UUID id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("기술 스택 정보를 찾을 수 없습니다.");
        }
        skillRepository.deleteById(id);
    }

    /**
     * 엔티티를 응답 DTO로 변환
     *
     * @param skill 기술 스택 엔티티
     * @return 기술 스택 응답 DTO
     */
    private SkillResponse toResponse(Skill skill) {
        return new SkillResponse(skill);
    }
}