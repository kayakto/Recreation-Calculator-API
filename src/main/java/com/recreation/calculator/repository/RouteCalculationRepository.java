package com.recreation.calculator.repository;

import com.recreation.calculator.domain.RouteCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteCalculationRepository extends JpaRepository<RouteCalculation, Long> {

    Optional<RouteCalculation> findByRouteId(Long routeId);
}
