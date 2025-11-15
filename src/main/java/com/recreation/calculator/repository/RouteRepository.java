package com.recreation.calculator.repository;

import com.recreation.calculator.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.calculation WHERE r.id = :routeId AND r.user.id = :userId")
    Optional<Route> findByIdAndUserIdWithCalculation(@Param("routeId") Long routeId, @Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Route r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
