package com.recreation.calculator.service;

import com.recreation.calculator.domain.Recommendation;
import com.recreation.calculator.dto.response.RecommendationResponse;
import com.recreation.calculator.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getAllRecommendations() {
        log.info("Fetching all recommendations");

        List<Recommendation> recommendations = recommendationRepository.findAll();

        return recommendations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendationsForRoute(
            List<Integer> ecologicalFactors, 
            List<Integer> managementFactors) {

        log.info("Fetching recommendations for ecological factors: {} and management factors: {}", 
                ecologicalFactors, managementFactors);

        if ((ecologicalFactors == null || ecologicalFactors.isEmpty()) && 
            (managementFactors == null || managementFactors.isEmpty())) {
            return new ArrayList<>();
        }

        List<Integer> ecoFactors = ecologicalFactors != null ? ecologicalFactors : List.of();
        List<Integer> mgmtFactors = managementFactors != null ? managementFactors : List.of();

        if (ecoFactors.isEmpty() && mgmtFactors.isEmpty()) {
            return new ArrayList<>();
        }

        List<Recommendation> recommendations = recommendationRepository
                .findRecommendationsByFactors(
                        ecoFactors.isEmpty() ? List.of(-1) : ecoFactors,
                        mgmtFactors.isEmpty() ? List.of(-1) : mgmtFactors
                );

        return recommendations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RecommendationResponse mapToResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .factorType(recommendation.getFactorType())
                .factorNumber(recommendation.getFactorNumber())
                .factorDescription(recommendation.getFactorDescription())
                .recommendationText(recommendation.getRecommendationText())
                .build();
    }
}
