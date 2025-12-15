package com.recreation.calculator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String login;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse of(Long id, String email, String login, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return UserResponse.builder()
                .id(id)
                .email(email)
                .login(login)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
