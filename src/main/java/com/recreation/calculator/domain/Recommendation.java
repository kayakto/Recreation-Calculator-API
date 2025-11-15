package com.recreation.calculator.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"factor_type", "factor_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "factor_type", nullable = false, length = 50)
    private String factorType;

    @Column(name = "factor_number", nullable = false)
    private Integer factorNumber;

    @Column(name = "factor_description", nullable = false, columnDefinition = "TEXT")
    private String factorDescription;

    @Column(name = "recommendation_text", nullable = false, columnDefinition = "TEXT")
    private String recommendationText;
}
