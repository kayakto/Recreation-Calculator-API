package com.recreation.calculator.service;

import com.recreation.calculator.domain.User;
import com.recreation.calculator.dto.response.AuthResponse;
import com.recreation.calculator.exception.ResourceNotFoundException;
import com.recreation.calculator.repository.UserRepository;
import com.recreation.calculator.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Неверный пароль");
        }

        String token = jwtTokenProvider.generateToken(email);
        log.info("User {} successfully authenticated", email);

        return AuthResponse.of(token, jwtExpirationMs, email);
    }

    @Transactional
    public AuthResponse register(String email, String password, String confirmPassword) {
        log.info("Registration attempt for email: {}", email);

        // Валидация паролей
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть не менее 6 символов");
        }

        // Проверка на существующего пользователя по email
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        // Проверка на существующего пользователя по login (который равен email)
        if (userRepository.findByLogin(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        // Создание нового пользователя с login = email
        User user = User.builder()
                .email(email)
                .login(email)  // ← Логин равен email
                .password(passwordEncoder.encode(password))  // ← Хешируем пароль
                .build();

        userRepository.save(user);

        // Генерируем токен
        String token = jwtTokenProvider.generateToken(email);
        log.info("User {} successfully registered", email);

        return AuthResponse.of(token, jwtExpirationMs, email);
    }
}
