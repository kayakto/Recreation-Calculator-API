package com.recreation.calculator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {

    private Long id;
    private String factorType;
    private Integer factorNumber;
    private String factorDescription;
    private String recommendationText;
}
