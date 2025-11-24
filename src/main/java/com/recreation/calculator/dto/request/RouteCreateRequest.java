package com.recreation.calculator.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCreateRequest {

    @NotBlank(message = "Название маршрута обязательно")
    @Size(max = 255, message = "Название маршрута не должно превышать 255 символов")
    @JsonProperty("routeName")
    private String routeName;

    @NotBlank(message = "Тип маршрута обязателен")
    @Pattern(regexp = "однодневный|многодневный", message = "Тип маршрута должен быть 'однодневный' или 'многодневный'")
    @JsonProperty("routeType")
    private String routeType;

    @NotBlank(message = "Тип маршрута по времени (fixed_time или unlimited_time) обязателен")
    @Pattern(regexp = "fixed_time|unlimited_time", message = "Тип маршрута должен быть 'fixed_time' или 'unlimited_time'")
    @JsonProperty("routeTimeType")
    private String routeTimeType;

    @NotNull(message = "Время доступности обязательно")
    @DecimalMin(value = "0.1", message = "Время доступности должно быть больше 0")
    @DecimalMax(value = "24.0", message = "Время доступности не должно превышать 24 часа")
    @JsonProperty("tSut")
    private BigDecimal tSut;

    @NotNull(message = "Дни туристического сезона обязательны")
    @Min(value = 1, message = "Дни туристического сезона должны быть больше 0")
    @JsonProperty("tSezon")
    private Integer tSezon;

    @NotNull(message = "Среднее количество человек в группе обязательно")
    @Min(value = 1, message = "Количество человек должно быть больше 0")
    @JsonProperty("gs")
    private Integer gs;

    @NotNull(message = "Количество дней прохождения маршрута обязательно")
    @Min(value = 1, message = "Количество дней должно быть больше 0")
    @JsonProperty("tl")
    private Integer tl;

    @NotEmpty(message = "Массив времени прохождения участков обязателен")
    @JsonProperty("tdArray")
    private List<BigDecimal> tdArray;

    @NotEmpty(message = "Массив длин участков обязателен")
    @JsonProperty("dtArray")
    private List<BigDecimal> dtArray;

    @NotEmpty(message = "Массив расстояний между группами обязателен")
    @JsonProperty("dgArray")
    private List<BigDecimal> dgArray;

    @NotEmpty(message = "Массив скоростей движения обязателен")
    @JsonProperty("vArray")
    private List<BigDecimal> vArray;

    @JsonProperty("ecologicalFactors")
    private List<Integer> ecologicalFactors;

    @JsonProperty("managementFactors")
    private List<Integer> managementFactors;
}
