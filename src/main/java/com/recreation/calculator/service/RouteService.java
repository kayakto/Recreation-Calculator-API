package com.recreation.calculator.service;

import com.recreation.calculator.domain.Route;
import com.recreation.calculator.domain.RouteCalculation;
import com.recreation.calculator.domain.User;
import com.recreation.calculator.dto.request.RouteCreateRequest;
import com.recreation.calculator.dto.response.RouteDetailResponse;
import com.recreation.calculator.dto.response.RouteListResponse;
import com.recreation.calculator.dto.response.RouteCalculationResponse;
import com.recreation.calculator.dto.response.RecommendationResponse;
import com.recreation.calculator.exception.ResourceNotFoundException;
import com.recreation.calculator.exception.UnauthorizedAccessException;
import com.recreation.calculator.repository.RouteRepository;
import com.recreation.calculator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final RouteCalculationService calculationService;
    private final RecommendationService recommendationService;

    @Transactional
    public RouteDetailResponse createRoute(RouteCreateRequest request, String userEmail) {
        log.info("Creating route '{}' for user: {}", request.getRouteName(), userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Route route = Route.builder()
                .user(user)
                .routeName(request.getRouteName())
                .routeType(request.getRouteType())
                .routeTimeType(request.getRouteTimeType())  // ← ДОБАВИТЬ если его нет
                .tSut(request.getTSut())
                .tSezon(request.getTSezon())
                .gs(request.getGs())
                .tl(request.getTl())
                .tdArray(request.getTdArray())
                .dtArray(request.getDtArray())
                .dgArray(request.getDgArray())
                .vArray(request.getVArray())
                .ecologicalFactors(request.getEcologicalFactors() != null ? request.getEcologicalFactors() : List.of())
                .managementFactors(request.getManagementFactors() != null ? request.getManagementFactors() : List.of())
                .build();

        // 1. Сохранить Route
        Route savedRoute = routeRepository.save(route);

        // 2. ПРАВИЛЬНО: Передать Route в calculationService
        RouteCalculation calculation = calculationService.saveCalculation(savedRoute);  // ✅

        // 3. Установить расчет в Route
        savedRoute.setCalculation(calculation);

        log.info("Route '{}' successfully created with ID: {}", savedRoute.getRouteName(), savedRoute.getId());

        return mapToDetailResponse(savedRoute);
    }

    @Transactional(readOnly = true)
    public List<RouteListResponse> getUserRoutes(String userEmail) {
        log.info("Fetching routes for user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<Route> routes = routeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return routes.stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RouteDetailResponse getRouteById(Long routeId, String userEmail) {
        log.info("Fetching route with ID: {} for user: {}", routeId, userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Route route = routeRepository.findByIdAndUserIdWithCalculation(routeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Маршрут не найден"));

        if (!route.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Доступ запрещён");
        }

        return mapToDetailResponse(route);
    }

    private RouteListResponse mapToListResponse(Route route) {
        RouteListResponse response = RouteListResponse.builder()
                .id(route.getId())
                .routeName(route.getRouteName())
                .routeType(route.getRouteType())
                .tl(route.getTl())
                .createdAt(route.getCreatedAt())
                .build();

        if (route.getCalculation() != null) {
            response.setBcc(route.getCalculation().getBcc());
            response.setPcc(route.getCalculation().getPcc());
            response.setRcc(route.getCalculation().getRcc());
        }

        return response;
    }

    private RouteDetailResponse mapToDetailResponse(Route route) {
        RouteDetailResponse response = RouteDetailResponse.builder()
                .id(route.getId())
                .routeName(route.getRouteName())
                .routeType(route.getRouteType())
                .routeTimeType(route.getRouteTimeType())
                .tSut(route.getTSut())
                .tSezon(route.getTSezon())
                .gs(route.getGs())
                .tl(route.getTl())
                .tdArray(route.getTdArray())
                .dtArray(route.getDtArray())
                .dgArray(route.getDgArray())
                .vArray(route.getVArray())
                .ecologicalFactors(route.getEcologicalFactors())
                .managementFactors(route.getManagementFactors())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();

        if (route.getCalculation() != null) {
            RouteCalculation calc = route.getCalculation();
            response.setCalculation(RouteCalculationResponse.builder()
                    .id(calc.getId())
                    .cfn(calc.getCfn())
                    .mCoefficient(calc.getMCoefficient())
                    .bcc(calc.getBcc())
                    .pcc(calc.getPcc())
                    .rcc(calc.getRcc())
                    .maxGroups(calc.getMaxGroups())
                    .createdAt(calc.getCreatedAt())
                    .build());
        }

        List<RecommendationResponse> recommendations = recommendationService
                .getRecommendationsForRoute(route.getEcologicalFactors(), route.getManagementFactors());
        response.setRecommendations(recommendations);

        return response;
    }

    @Transactional
    public void deleteRoute(Long routeId, String userEmail) {
        // 1. Проверить, что маршрут принадлежит пользователю
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Route route = routeRepository.findByIdAndUserIdWithCalculation(routeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Маршрут не найден"));

        if (!route.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Доступ запрещён");
        }

        // 2. Удалить маршрут (расчёт удалится каскадно)
        routeRepository.delete(route);
    }
}
