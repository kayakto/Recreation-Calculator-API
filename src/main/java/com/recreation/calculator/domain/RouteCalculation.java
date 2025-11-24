package com.recreation.calculator.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "route_calculations")
public class RouteCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, unique = true)
    private Route route;

    // ===== РАССЧИТАННЫЕ КОЭФФИЦИЕНТЫ =====
    @Column(name = "cfn", nullable = false)
    private BigDecimal cfn;  // Рассчитано из ecologicalFactors

    @Column(name = "m_coefficient", nullable = false)
    private BigDecimal mCoefficient;  // Рассчитано из managementFactors

    // ===== РЕЗУЛЬТАТЫ РАСЧЕТА (могут быть NULL) =====
    @Column(name = "bcc")
    private Integer bcc;  // NULL если расчет не применим для типа маршрута

    @Column(name = "pcc")
    private Integer pcc;  // NULL для неограниченного времени

    @Column(name = "rcc")
    private Integer rcc;  // NULL для неограниченного времени

    @Column(name = "max_groups")
    private Integer maxGroups;  // NULL для неограниченного времени

    // ===== МЕТАДАННЫЕ =====
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
