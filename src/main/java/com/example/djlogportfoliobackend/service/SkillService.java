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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(SkillResponse::new)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public SkillResponse updateSkill(UUID id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기술 스택 정보를 찾을 수 없습니다."));

        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setProficiency(request.getProficiency());

        return new SkillResponse(skill);
    }

    @Transactional
    public void deleteSkill(UUID id) {
        if (!skillRepository.existsById(id)) {
            throw new RuntimeException("기술 스택 정보를 찾을 수 없습니다.");
        }
        skillRepository.deleteById(id);
    }
}