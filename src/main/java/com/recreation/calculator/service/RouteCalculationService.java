package com.recreation.calculator.service;

import com.recreation.calculator.domain.RouteCalculation;
import com.recreation.calculator.repository.RouteCalculationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteCalculationService {

    private final RouteCalculationRepository calculationRepository;

    @Transactional
    public RouteCalculation saveCalculation(RouteCalculation calculation) {
        log.info("Saving calculation for route ID: {}", calculation.getRoute().getId());
        return calculationRepository.save(calculation);
    }
}
