package com.cricketacademy.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record NetBookingRequest(
        @NotNull Long facilityId, @NotNull Instant startTime, @NotNull Instant endTime, String notes) {}
