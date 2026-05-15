package com.cricketacademy.web.dto;

import com.cricketacademy.domain.fixture.MatchFixtureStatus;
import java.time.Instant;

public record MatchFixtureResponse(Long id, String title, Long batchId, Instant scheduledAt, MatchFixtureStatus status) {}
