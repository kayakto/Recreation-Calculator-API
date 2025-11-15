package com.recreation.calculator.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "cfn", nullable = false)
    private BigDecimal cfn;

    @Column(name = "m_coefficient", nullable = false)  // ← ВАЖНО: nullable = false
    private BigDecimal mCoefficient;

    @Column(name = "bcc", nullable = false)
    private Integer bcc;

    @Column(name = "pcc", nullable = false)
    private Integer pcc;

    @Column(name = "rcc", nullable = false)
    private Integer rcc;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
