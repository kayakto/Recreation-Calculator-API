package com.recreation.calculator.service;

import com.recreation.calculator.domain.Route;
import com.recreation.calculator.domain.RouteCalculation;
import com.recreation.calculator.repository.RouteCalculationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteCalculationService {

    private final RouteCalculationRepository routeCalculationRepository;

    @Transactional
    public RouteCalculation saveCalculation(Route route) {
        RouteCalculation calculation = new RouteCalculation();
        calculation.setRoute(route);

        // 1. ВСЕГДА РАССЧИТЫВАЕМ CFN и M
        BigDecimal cfn = calculateCfn(route.getEcologicalFactors());
        calculation.setCfn(cfn);

        BigDecimal mCoefficient = calculateMCoefficient(route.getManagementFactors());
        calculation.setMCoefficient(mCoefficient);

        // 2. В зависимости от типа маршрута - рассчитываем разные параметры
        if (route.isFixedTimeRoute()) {
            calculateFixedTimeMetrics(route, calculation, cfn, mCoefficient);
        } else if (route.isUnlimitedTimeRoute()) {
            calculateUnlimitedTimeMetrics(route, calculation, cfn, mCoefficient);
        }

        // 3. Сохранить в БД
        return routeCalculationRepository.save(calculation);
    }

    // ====================================================
    // РАСЧЕТ КОЭФФИЦИЕНТОВ ИЗ JSONB
    // ====================================================

    private BigDecimal calculateCfn(Object ecologicalFactors) {
        if (ecologicalFactors == null) {
            return new BigDecimal("1.0");
        }

        try {
            BigDecimal cfn = new BigDecimal("1.0");

            if (ecologicalFactors instanceof JsonNode) {
                JsonNode node = (JsonNode) ecologicalFactors;
                for (JsonNode factor : node) {
                    BigDecimal impact = new BigDecimal(factor.get("impact").asText());
                    cfn = cfn.add(impact);
                }
            }

            // Ограничиваем [0.1, 1.0]
            if (cfn.compareTo(new BigDecimal("0.1")) < 0) {
                cfn = new BigDecimal("0.1");
            }
            if (cfn.compareTo(new BigDecimal("1.0")) > 0) {
                cfn = new BigDecimal("1.0");
            }

            return cfn.setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            log.warn("Ошибка расчета CFN: {}", e.getMessage());
            return new BigDecimal("1.0");
        }
    }

    private BigDecimal calculateMCoefficient(Object managementFactors) {
        if (managementFactors == null) {
            return new BigDecimal("1.0");
        }

        try {
            BigDecimal mCoefficient = new BigDecimal("1.0");

            if (managementFactors instanceof JsonNode) {
                JsonNode node = (JsonNode) managementFactors;
                for (JsonNode factor : node) {
                    BigDecimal efficiency = new BigDecimal(factor.get("efficiency").asText());
                    mCoefficient = mCoefficient.add(efficiency);
                }
            }

            // Ограничиваем [0.1, 1.0]
            if (mCoefficient.compareTo(new BigDecimal("0.1")) < 0) {
                mCoefficient = new BigDecimal("0.1");
            }
            if (mCoefficient.compareTo(new BigDecimal("1.0")) > 0) {
                mCoefficient = new BigDecimal("1.0");
            }

            return mCoefficient.setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            log.warn("Ошибка расчета M коэффициента: {}", e.getMessage());
            return new BigDecimal("1.0");
        }
    }

    // ====================================================
    // МЕТРИКИ ДЛЯ ФИКСИРОВАННОГО ВРЕМЕНИ
    // ====================================================

    private void calculateFixedTimeMetrics(Route route, RouteCalculation calc,
                                           BigDecimal cfn, BigDecimal mCoefficient) {
        try {
            // Рассчитываем среднее T_d
            BigDecimal avgTd = calculateAverageTd(route.getTdArray());

            log.debug("avgTd: {}", avgTd);

            if (avgTd == null || avgTd.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Ошибка: T_d <= 0 или NULL для маршрута {}", route.getId());
                return;
            }

            // max_groups = INT(T_сут / T_d)
            Integer maxGroups = route.getTSut()
                    .divide(avgTd, 2, BigDecimal.ROUND_DOWN)
                    .intValue();
            calc.setMaxGroups(maxGroups);

            log.debug("maxGroups установлен: {}", maxGroups);

            // BCC = INT(T_сут / T_d) * GS
            Integer bcc = maxGroups * route.getGs();
            calc.setBcc(bcc);

            // PCC = INT(BCC * CFN)
            Integer pcc = BigDecimal.valueOf(bcc)
                    .multiply(cfn)
                    .setScale(0, BigDecimal.ROUND_HALF_UP)
                    .intValue();
            calc.setPcc(pcc);

            // RCC = INT(PCC * M)
            Integer rcc = BigDecimal.valueOf(pcc)
                    .multiply(mCoefficient)
                    .setScale(0, BigDecimal.ROUND_DOWN)
                    .intValue();
            calc.setRcc(rcc);

            log.info("Расчеты для fixed_time: bcc={}, pcc={}, rcc={}, maxGroups={}",
                    bcc, pcc, rcc, maxGroups);

        } catch (Exception e) {
            log.error("Ошибка расчета метрик для фиксированного времени: {}", e.getMessage(), e);
        }
    }

// ====================================================
// ВСПОМОГАТЕЛЬНЫЙ МЕТОД - ИСПРАВЛЕННЫЙ
// ====================================================

    private BigDecimal calculateAverageTd(Object tdArray) {
        if (tdArray == null) {
            log.warn("tdArray is NULL");
            return BigDecimal.ZERO;
        }

        try {
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;

            // ГЛАВНОЕ ИЗМЕНЕНИЕ: tdArray из Route это List<BigDecimal>
            if (tdArray instanceof List) {
                @SuppressWarnings("unchecked")
                List<BigDecimal> list = (List<BigDecimal>) tdArray;

                if (list.isEmpty()) {
                    log.warn("tdArray is empty");
                    return BigDecimal.ZERO;
                }

                for (BigDecimal item : list) {
                    if (item != null) {
                        sum = sum.add(item);
                        count++;
                    }
                }
            } else if (tdArray instanceof JsonNode) {
                JsonNode node = (JsonNode) tdArray;
                for (JsonNode item : node) {
                    sum = sum.add(new BigDecimal(item.asText()));
                    count++;
                }
            } else {
                log.warn("tdArray имеет неожиданный тип: {}", tdArray.getClass());
                return BigDecimal.ZERO;
            }

            if (count == 0) {
                log.warn("Нет элементов в tdArray");
                return BigDecimal.ZERO;
            }

            BigDecimal average = sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
            log.debug("calculateAverageTd: sum={}, count={}, avg={}", sum, count, average);

            return average;

        } catch (Exception e) {
            log.error("Ошибка расчета среднего T_d: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    // ====================================================
    // МЕТРИКИ ДЛЯ НЕОГРАНИЧЕННОГО ВРЕМЕНИ
    // ====================================================

    private void calculateUnlimitedTimeMetrics(Route route, RouteCalculation calc,
                                               BigDecimal cfn, BigDecimal mCoefficient) {
        try {
            if (route.getTl() == null || route.getTl() == 0) {
                log.warn("Ошибка: Tl <= 0 для маршрута {}", route.getId());
                return;
            }

            // BCC = INT(T_sezon / Tl) * GS
            Integer bcc = (route.getTSezon() / route.getTl()) * route.getGs();
            calc.setBcc(bcc);

            // Для неограниченного времени остальные поля остаются NULL
            calc.setMaxGroups(null);
            calc.setPcc(null);
            calc.setRcc(null);

        } catch (Exception e) {
            log.error("Ошибка расчета метрик для неограниченного времени: {}", e.getMessage());
        }
    }
}
