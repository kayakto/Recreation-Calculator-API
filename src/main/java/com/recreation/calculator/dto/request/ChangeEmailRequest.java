package com.recreation.calculator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeEmailRequest {

    @NotBlank(message = "Новый email обязателен")
    private String newEmail;

    @NotBlank(message = "Пароль обязателен для подтверждения")
    private String password;
}
