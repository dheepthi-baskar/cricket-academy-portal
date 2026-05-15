package com.cricketacademy.web.dto;

import java.math.BigDecimal;

public record BillingStatementResponse(Long id, Long playerId, String playerName, String periodLabel, BigDecimal amountDue) {}
