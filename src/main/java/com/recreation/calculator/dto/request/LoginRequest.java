package com.recreation.calculator.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email обязателен")
//    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
