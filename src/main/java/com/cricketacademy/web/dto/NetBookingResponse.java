package com.cricketacademy.web.dto;

import java.time.Instant;

public record NetBookingResponse(
        Long id, Long facilityId, String facilityName, Instant startTime, Instant endTime, String notes) {}
