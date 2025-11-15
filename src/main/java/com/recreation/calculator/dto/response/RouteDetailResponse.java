package com.recreation.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDetailResponse {

    private Long id;
    private String routeName;
    private String routeType;

    // Основные параметры
    private BigDecimal tSut;
    private Integer tSezon;
    private Integer gs;
    private Integer tl;

    // Массивы данных
    private List<BigDecimal> tdArray;
    private List<BigDecimal> dtArray;
    private List<BigDecimal> dgArray;
    private List<BigDecimal> vArray;

    // Выбранные факторы
    private List<Integer> ecologicalFactors;
    private List<Integer> managementFactors;

    // Расчёты
    private RouteCalculationResponse calculation;

    // Рекомендации
    private List<RecommendationResponse> recommendations;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
