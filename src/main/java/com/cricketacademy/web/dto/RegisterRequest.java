package com.cricketacademy.web.dto;

import com.cricketacademy.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotNull Role role,
        Long batchId) {}
