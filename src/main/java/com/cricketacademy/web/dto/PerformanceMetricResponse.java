package com.cricketacademy.web.dto;

import java.time.LocalDate;

public record PerformanceMetricResponse(
        Long id,
        Long playerId,
        LocalDate metricDate,
        Double bowlingSpeed,
        Integer runsScored,
        Integer wicketsTaken,
        Integer fitnessScore,
        String coachNotes) {}
