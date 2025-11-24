package com.recreation.calculator.controller;

import com.recreation.calculator.dto.request.RouteCreateRequest;
import com.recreation.calculator.dto.response.RouteDetailResponse;
import com.recreation.calculator.dto.response.RouteListResponse;
import com.recreation.calculator.dto.response.RecommendationResponse;
import com.recreation.calculator.service.RecommendationService;
import com.recreation.calculator.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Routes", description = "API для управления маршрутами и расчётами рекреационной ёмкости")
@SecurityRequirement(name = "bearerAuth")
public class RouteController {

    private final RouteService routeService;
    private final RecommendationService recommendationService;

    // ← НОВЫЙ МЕТОД для получения username из Security контекста
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("Пользователь не авторизован");
    }

    @PostMapping
    @Operation(
            summary = "Сохранение расчёта маршрута",
            description = "Сохраняет введённые пользователем данные маршрута и результаты расчёта в базу данных"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Маршрут успешно сохранён",
                    content = @Content(schema = @Schema(implementation = RouteDetailResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<RouteDetailResponse> createRoute(
            @Valid @RequestBody RouteCreateRequest request) {

        String userEmail = getCurrentUsername();
        log.info("POST /api/v1/routes - Creating route for user: {}", userEmail);

        RouteDetailResponse response = routeService.createRoute(request, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Получение списка расчётов текущего пользователя",
            description = "Возвращает список всех сохранённых расчётов, созданных авторизованным пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список маршрутов успешно получен",
                    content = @Content(schema = @Schema(implementation = RouteListResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<RouteListResponse>> getUserRoutes() {

        String userEmail = getCurrentUsername();
        log.info("GET /api/v1/routes - Fetching routes for user: {}", userEmail);

        List<RouteListResponse> routes = routeService.getUserRoutes(userEmail);

        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{routeId}")
    @Operation(
            summary = "Получение детальной информации о расчёте",
            description = "Возвращает полные данные конкретного расчёта пользователя по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Детали маршрута успешно получены",
                    content = @Content(schema = @Schema(implementation = RouteDetailResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Маршрут не найден")
    })
    public ResponseEntity<RouteDetailResponse> getRouteById(
            @Parameter(description = "ID маршрута", required = true) @PathVariable Long routeId) {

        String userEmail = getCurrentUsername();
        log.info("GET /api/v1/routes/{} - Fetching route details for user: {}", routeId, userEmail);

        RouteDetailResponse response = routeService.getRouteById(routeId, userEmail);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations")
    @Operation(
            summary = "Получение всех рекомендаций",
            description = "Возвращает справочник всех доступных рекомендаций для экологических и управленческих факторов"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рекомендации успешно получены",
                    content = @Content(schema = @Schema(implementation = RecommendationResponse.class))
            )
    })
    public ResponseEntity<List<RecommendationResponse>> getAllRecommendations() {
        log.info("GET /api/v1/routes/recommendations - Fetching all recommendations");

        List<RecommendationResponse> recommendations = recommendationService.getAllRecommendations();

        return ResponseEntity.ok(recommendations);
    }

    @DeleteMapping("/{routeId}")
    @Operation(summary = "Удалить маршрут пользователя с расчетом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Маршрут и расчет удалены"),
            @ApiResponse(responseCode = "404", description = "Маршрут не найден"),
            @ApiResponse(responseCode = "401", description = "Нет доступа"),
            @ApiResponse(responseCode = "403", description = "Нет прав")
    })
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {
        String userEmail = getCurrentUsername();
        log.info("DELETE /api/v1/routes/{} - user: {}", routeId, userEmail);
        routeService.deleteRoute(routeId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
