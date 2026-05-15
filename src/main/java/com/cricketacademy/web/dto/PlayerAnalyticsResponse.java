package com.cricketacademy.web.dto;

import java.util.List;

public record PlayerAnalyticsResponse(
        double overallAverageBowlingSpeed,
        int totalRunsInRange,
        double averageFitnessScore,
        List<WeeklyAggregatePoint> weeklySeries) {}
