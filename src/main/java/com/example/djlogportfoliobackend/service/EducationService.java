package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.EducationResponse;
import com.example.djlogportfoliobackend.entity.Education;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.exception.ResourceNotFoundException;
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
 * 학력 데이터의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 학력 목록 조회
     * 재학 기간을 기준으로 내림차순 정렬하여 반환합니다.
     *
     * @return 학력 목록
     */
    public List<EducationResponse> getAllEducations() {
        return educationRepository.findAllByOrderByPeriodDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 학력 정보 생성
     *
     * @param request 학력 생성 요청 데이터
     * @return 생성된 학력 정보
     * @throws ResourceNotFoundException 프로필이 존재하지 않을 경우
     */
    @Transactional
    public EducationResponse createEducation(EducationRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("프로필이 존재하지 않습니다."));

        Education education = new Education(
                request.getSchool(),
                request.getMajor(),
                request.getPeriod(),
                request.getDegree(),
                profile
        );

        Education savedEducation = educationRepository.save(education);
        return toResponse(savedEducation);
    }

    /**
     * 기존 학력 정보 수정
     *
     * @param id 수정할 학력 ID
     * @param request 수정할 학력 데이터
     * @return 수정된 학력 정보
     * @throws ResourceNotFoundException 학력 정보를 찾을 수 없을 경우
     */
    @Transactional
    public EducationResponse updateEducation(UUID id, EducationRequest request) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("학력 정보를 찾을 수 없습니다."));

        education.setSchool(request.getSchool());
        education.setMajor(request.getMajor());
        education.setPeriod(request.getPeriod());
        education.setDegree(request.getDegree());

        return toResponse(education);
    }

    /**
     * 학력 정보 삭제
     *
     * @param id 삭제할 학력 ID
     * @throws ResourceNotFoundException 학력 정보를 찾을 수 없을 경우
     */
    @Transactional
    public void deleteEducation(UUID id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("학력 정보를 찾을 수 없습니다.");
        }
        educationRepository.deleteById(id);
    }

    /**
     * 엔티티를 응답 DTO로 변환
     *
     * @param education 학력 엔티티
     * @return 학력 응답 DTO
     */
    private EducationResponse toResponse(Education education) {
        return new EducationResponse(education);
    }
}