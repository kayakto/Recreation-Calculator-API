package com.recreation.calculator.controller;

import com.recreation.calculator.dto.request.ChangeEmailRequest;
import com.recreation.calculator.dto.request.ChangePasswordRequest;
import com.recreation.calculator.dto.response.UserResponse;
import com.recreation.calculator.security.JwtTokenProvider;
import com.recreation.calculator.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "API для управления профилем пользователя")
public class UserController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/profile")
    @Operation(
            summary = "Получить профиль пользователя",
            security = @SecurityRequirement(name = "bearerAuth") // ← ИСПРАВЛЕНО: "bearerAuth"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль получен успешно",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserResponse> getProfile(HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        log.info("Profile request for user: {}", email);

        UserResponse profile = authService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/email")
    @Operation(
            summary = "Изменить email",
            security = @SecurityRequirement(name = "bearerAuth") // ← ИСПРАВЛЕНО: "bearerAuth"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email изменен успешно",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Не авторизован или неверный пароль")
    })
    public ResponseEntity<UserResponse> changeEmail(
            @Valid @RequestBody ChangeEmailRequest request,
            HttpServletRequest httpRequest) {

        String currentEmail = extractEmailFromToken(httpRequest);
        log.info("Email change request for user: {}", currentEmail);

        UserResponse updated = authService.changeEmail(currentEmail, request.getNewEmail(), request.getPassword());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/password")
    @Operation(
            summary = "Изменить пароль",
            security = @SecurityRequirement(name = "bearerAuth") // ← ИСПРАВЛЕНО: "bearerAuth"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль изменен успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Не авторизован или неверный старый пароль")
    })
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {

        String email = extractEmailFromToken(httpRequest);
        log.info("Password change request for user: {}", email);

        authService.changePassword(email, request.getOldPassword(),
                request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok().build();
    }

    private String extractEmailFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new IllegalArgumentException("Токен не найден");
        }
        return jwtTokenProvider.getEmailFromToken(token);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
