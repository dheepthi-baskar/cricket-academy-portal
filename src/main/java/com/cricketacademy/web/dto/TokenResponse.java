package com.cricketacademy.web.dto;

public record TokenResponse(String accessToken, Long userId, String role) {}
