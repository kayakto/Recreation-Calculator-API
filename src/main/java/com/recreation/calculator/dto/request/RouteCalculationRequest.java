package com.recreation.calculator.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCalculationRequest {

    @NotNull(message = "cfn обязателен")
    @DecimalMin(value = "0", message = "cfn должен быть >= 0")
    @DecimalMax(value = "1", message = "cfn должен быть <= 1")
    @JsonProperty("cfn")
    private BigDecimal cfn;

    @NotNull(message = "mCoefficient обязателен")
    @DecimalMin(value = "0.01", message = "mCoefficient должен быть > 0")
    @DecimalMax(value = "1", message = "mCoefficient должен быть <= 1")
    @JsonProperty("mCoefficient")
    private BigDecimal mCoefficient;

    @NotNull(message = "bcc обязателен")
    @Min(value = 0, message = "bcc не должен быть отрицательным")
    @JsonProperty("bcc")
    private Integer bcc;

    @NotNull(message = "pcc обязателен")
    @Min(value = 0, message = "pcc не должен быть отрицательным")
    @JsonProperty("pcc")
    private Integer pcc;

    @NotNull(message = "rcc обязателен")
    @Min(value = 0, message = "rcc не должен быть отрицательным")
    @JsonProperty("rcc")
    private Integer rcc;
}
