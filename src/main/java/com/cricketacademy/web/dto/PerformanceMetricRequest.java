package com.cricketacademy.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PerformanceMetricRequest(
        @NotNull LocalDate metricDate,
        Double bowlingSpeed,
        Integer runsScored,
        Integer wicketsTaken,
        @Min(1) @Max(10) Integer fitnessScore,
        String coachNotes) {}
