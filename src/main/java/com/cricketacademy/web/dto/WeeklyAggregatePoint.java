package com.cricketacademy.web.dto;

import java.time.LocalDate;

public record WeeklyAggregatePoint(LocalDate weekStartingMonday, int totalRuns, double avgBowlingSpeed) {}
