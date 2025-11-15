package com.recreation.calculator.service;

import com.recreation.calculator.domain.User;
import com.recreation.calculator.dto.response.AuthResponse;
import com.recreation.calculator.exception.ResourceNotFoundException;
import com.recreation.calculator.repository.UserRepository;
import com.recreation.calculator.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        log.info("Login attempt for user: {}", email);

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new ResourceNotFoundException("Email и пароль обязательны");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // ← ПРОСТО сравниваем строки БЕЗ BCrypt
        if (!password.equals(user.getPassword())) {
            throw new ResourceNotFoundException("Неверный пароль");
        }

        String token = jwtTokenProvider.generateToken(email);

        log.info("User {} successfully authenticated", email);

        return AuthResponse.of(token, jwtExpirationMs, email);
    }
}
