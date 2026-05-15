package com.cricketacademy.service;

import com.cricketacademy.domain.PerformanceMetric;
import com.cricketacademy.repo.PerformanceMetricRepository;
import com.cricketacademy.web.dto.PlayerAnalyticsResponse;
import com.cricketacademy.web.dto.WeeklyAggregatePoint;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PerformanceAnalyticsService {

    private final PerformanceMetricRepository metrics;

    public PerformanceAnalyticsService(PerformanceMetricRepository metrics) {
        this.metrics = metrics;
    }

    public PlayerAnalyticsResponse summarize(Long playerId, LocalDate fromInclusive, LocalDate toInclusive) {
        List<PerformanceMetric> data = metrics.findByPlayerIdAndMetricDateBetweenOrderByMetricDateAsc(
                playerId, fromInclusive, toInclusive);

        double avgBowling = data.stream()
                .map(PerformanceMetric::getBowlingSpeed)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        int totalRuns = data.stream()
                .mapToInt(m -> Optional.ofNullable(m.getRunsScored()).orElse(0))
                .sum();

        double avgFitness = data.stream()
                .map(PerformanceMetric::getFitnessScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        Map<LocalDate, List<PerformanceMetric>> byWeek = data.stream()
                .collect(Collectors.groupingBy(m -> m.getMetricDate().with(DayOfWeek.MONDAY)));

        List<WeeklyAggregatePoint> series = byWeek.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> {
                    LocalDate weekStart = entry.getKey();
                    List<PerformanceMetric> slice = entry.getValue();
                    int runs = slice.stream()
                            .mapToInt(m -> Optional.ofNullable(m.getRunsScored()).orElse(0))
                            .sum();
                    double weekAvgSpeed = slice.stream()
                            .map(PerformanceMetric::getBowlingSpeed)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    return new WeeklyAggregatePoint(weekStart, runs, weekAvgSpeed);
                })
                .toList();

        return new PlayerAnalyticsResponse(avgBowling, totalRuns, avgFitness, series);
    }
}
