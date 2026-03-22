package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * Q&A 순서 변경 단건 요청 DTO
 */
@Data
public class ProjectQnAOrderUpdateRequest {

    /**
     * 순서를 변경할 Q&A ID
     */
    @NotNull(message = "Q&A ID는 필수입니다")
    private UUID id;

    /**
     * 변경할 표시 순서
     */
    @NotNull(message = "displayOrder는 필수입니다")
    @Min(value = 0, message = "displayOrder는 0 이상이어야 합니다")
    private Integer displayOrder;
}
