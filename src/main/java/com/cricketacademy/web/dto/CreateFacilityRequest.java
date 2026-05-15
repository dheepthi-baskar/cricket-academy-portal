package com.cricketacademy.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFacilityRequest(@NotBlank String name, String description) {}
