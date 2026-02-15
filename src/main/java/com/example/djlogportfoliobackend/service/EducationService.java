package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.EducationResponse;
import com.example.djlogportfoliobackend.entity.Education;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.repository.EducationRepository;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 학력 정보 관리 서비스
 * 학력 정보의 CRUD 작업을 처리하고 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 학력 정보 조회
     * 기간순으로 내림차순 정렬하여 반환합니다.
     * @return 학력 정보 리스트
     */
    public List<EducationResponse> getAllEducations() {
        return educationRepository.findAllByOrderByPeriodDesc().stream()
                .map(EducationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 학력 정보 등록
     * @param request 학력 정보 등록 요청 DTO
     * @return 등록된 학력 정보 응답 DTO
     * @throws RuntimeException 프로필이 존재하지 않을 때
     */
    @Transactional
    public EducationResponse createEducation(EducationRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("프로필이 존재하지 않습니다."));

        Education education = new Education(
                request.getSchool(),
                request.getMajor(),
                request.getPeriod(),
                request.getDegree(),
                profile
        );

        Education savedEducation = educationRepository.save(education);
        return new EducationResponse(savedEducation);
    }

    /**
     * 학력 정보 수정
     * @param id 수정할 학력 정보 ID
     * @param request 학력 정보 수정 요청 DTO
     * @return 수정된 학력 정보 응답 DTO
     * @throws RuntimeException 학력 정보를 찾을 수 없을 때
     */
    @Transactional
    public EducationResponse updateEducation(UUID id, EducationRequest request) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학력 정보를 찾을 수 없습니다."));

        education.setSchool(request.getSchool());
        education.setMajor(request.getMajor());
        education.setPeriod(request.getPeriod());
        education.setDegree(request.getDegree());

        return new EducationResponse(education);
    }

    /**
     * 학력 정보 삭제
     * @param id 삭제할 학력 정보 ID
     * @throws RuntimeException 학력 정보를 찾을 수 없을 때
     */
    @Transactional
    public void deleteEducation(UUID id) {
        if (!educationRepository.existsById(id)) {
            throw new RuntimeException("학력 정보를 찾을 수 없습니다.");
        }
        educationRepository.deleteById(id);
    }
}