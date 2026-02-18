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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;

    public List<EducationResponse> getAllEducations() {
        return educationRepository.findAllByOrderByPeriodDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void deleteEducation(UUID id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("학력 정보를 찾을 수 없습니다.");
        }
        educationRepository.deleteById(id);
    }

    private EducationResponse toResponse(Education education) {
        return new EducationResponse(education);
    }
}