package com.cricketacademy.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBatchRequest(@NotBlank String name, String description, @NotNull Long coachUserId) {}
