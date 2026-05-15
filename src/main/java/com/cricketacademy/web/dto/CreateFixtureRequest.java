package com.cricketacademy.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateFixtureRequest(@NotBlank String title, @NotNull Long batchId, @NotNull Instant scheduledAt) {}
