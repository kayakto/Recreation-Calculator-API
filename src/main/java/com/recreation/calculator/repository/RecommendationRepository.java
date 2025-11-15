package com.recreation.calculator.repository;

import com.recreation.calculator.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByFactorTypeAndFactorNumberIn(String factorType, List<Integer> factorNumbers);

    @Query("SELECT r FROM Recommendation r WHERE " +
           "(r.factorType = 'ecological' AND r.factorNumber IN :ecologicalFactors) OR " +
           "(r.factorType = 'management' AND r.factorNumber IN :managementFactors)")
    List<Recommendation> findRecommendationsByFactors(
            @Param("ecologicalFactors") List<Integer> ecologicalFactors,
            @Param("managementFactors") List<Integer> managementFactors);
}
