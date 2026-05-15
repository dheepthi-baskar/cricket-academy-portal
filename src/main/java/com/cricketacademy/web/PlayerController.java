package com.cricketacademy.web;

import com.cricketacademy.domain.Role;
import com.cricketacademy.repo.PlayerRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.service.MatchFixtureApplicationService;
import com.cricketacademy.service.PerformanceAnalyticsService;
import com.cricketacademy.service.PerformanceCommandService;
import com.cricketacademy.web.dto.MatchFixtureResponse;
import com.cricketacademy.web.dto.PerformanceMetricResponse;
import com.cricketacademy.web.dto.PlayerAnalyticsResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerRepository players;
    private final PerformanceCommandService performanceCommandService;
    private final PerformanceAnalyticsService performanceAnalyticsService;
    private final MatchFixtureApplicationService matchFixtureApplicationService;

    public PlayerController(
            PlayerRepository players,
            PerformanceCommandService performanceCommandService,
            PerformanceAnalyticsService performanceAnalyticsService,
            MatchFixtureApplicationService matchFixtureApplicationService) {
        this.players = players;
        this.performanceCommandService = performanceCommandService;
        this.performanceAnalyticsService = performanceAnalyticsService;
        this.matchFixtureApplicationService = matchFixtureApplicationService;
    }

    @GetMapping("/me/metrics")
    public ResponseEntity<List<PerformanceMetricResponse>> myMetrics(@AuthenticationPrincipal AcademyUserDetails user) {
        long playerId = requirePlayerId(user);
        return ResponseEntity.ok(performanceCommandService.listForPlayer(playerId, user));
    }

    @GetMapping("/me/analytics")
    public ResponseEntity<PlayerAnalyticsResponse> myAnalytics(
            @AuthenticationPrincipal AcademyUserDetails user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        long playerId = requirePlayerId(user);
        return ResponseEntity.ok(performanceAnalyticsService.summarize(playerId, from, to));
    }

    @GetMapping("/me/fixtures")
    public ResponseEntity<List<MatchFixtureResponse>> myFixtures(@AuthenticationPrincipal AcademyUserDetails user) {
        return ResponseEntity.ok(matchFixtureApplicationService.listVisible(user));
    }

    private long requirePlayerId(AcademyUserDetails user) {
        if (user.getRole() != Role.PLAYER) {
            throw new AccessDeniedException("Endpoint is intended for players");
        }
        return players.findByUser_EmailIgnoreCase(user.getUsername())
                .orElseThrow(() -> new IllegalStateException("Player profile missing"))
                .getId();
    }
}
