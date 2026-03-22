package com.example.djlogportfoliobackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Q&A 순서 일괄 변경 요청 DTO
 */
@Data
public class ProjectQnAOrderBulkUpdateRequest {

    /**
     * 변경할 Q&A 순서 목록
     */
    @Valid
    @NotEmpty(message = "Q&A 순서 목록은 비어 있을 수 없습니다")
    private List<ProjectQnAOrderUpdateRequest> qnaOrders;
}
