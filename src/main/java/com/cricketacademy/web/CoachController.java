package com.cricketacademy.web;

import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.service.CoachBillingService;
import com.cricketacademy.service.MatchFixtureApplicationService;
import com.cricketacademy.service.NetBookingService;
import com.cricketacademy.service.PerformanceCommandService;
import com.cricketacademy.web.dto.BillingStatementResponse;
import com.cricketacademy.web.dto.MatchFixtureResponse;
import com.cricketacademy.web.dto.NetBookingRequest;
import com.cricketacademy.web.dto.NetBookingResponse;
import com.cricketacademy.repo.FacilityRepository;
import com.cricketacademy.repo.PlayerRepository;
import com.cricketacademy.web.dto.FacilityResponse;
import com.cricketacademy.web.dto.PerformanceMetricRequest;
import com.cricketacademy.web.dto.PerformanceMetricResponse;
import com.cricketacademy.web.dto.PlayerSummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coach")
public class CoachController {

    private final PlayerRepository players;
    private final FacilityRepository facilities;
    private final PerformanceCommandService performanceCommandService;
    private final CoachBillingService coachBillingService;
    private final NetBookingService netBookingService;
    private final MatchFixtureApplicationService matchFixtureApplicationService;

    public CoachController(
            PlayerRepository players,
            FacilityRepository facilities,
            PerformanceCommandService performanceCommandService,
            CoachBillingService coachBillingService,
            NetBookingService netBookingService,
            MatchFixtureApplicationService matchFixtureApplicationService) {
        this.players = players;
        this.facilities = facilities;
        this.performanceCommandService = performanceCommandService;
        this.coachBillingService = coachBillingService;
        this.netBookingService = netBookingService;
        this.matchFixtureApplicationService = matchFixtureApplicationService;
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<FacilityResponse>> listFacilities() {
        return ResponseEntity.ok(facilities.findAll().stream()
                .map(f -> new FacilityResponse(f.getId(), f.getName(), f.getDescription()))
                .toList());
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerSummaryResponse>> listPlayers() {
        return ResponseEntity.ok(players.findAllWithBatch().stream()
                .map(p -> new PlayerSummaryResponse(p.getId(), p.getDisplayName(), p.getBatch().getName()))
                .toList());
    }

    @PostMapping("/players/{playerId}/metrics")
    public ResponseEntity<PerformanceMetricResponse> recordMetric(
            @AuthenticationPrincipal AcademyUserDetails user,
            @PathVariable Long playerId,
            @Valid @RequestBody PerformanceMetricRequest request) {
        return ResponseEntity.ok(performanceCommandService.recordMetric(playerId, request, user));
    }

    @GetMapping("/players/{playerId}/metrics")
    public ResponseEntity<List<PerformanceMetricResponse>> listMetrics(
            @AuthenticationPrincipal AcademyUserDetails user, @PathVariable Long playerId) {
        return ResponseEntity.ok(performanceCommandService.listForPlayer(playerId, user));
    }

    @GetMapping("/billing/summaries")
    public ResponseEntity<List<BillingStatementResponse>> billing(@AuthenticationPrincipal AcademyUserDetails user) {
        return ResponseEntity.ok(coachBillingService.summariesForCoach(user));
    }

    @PostMapping("/bookings")
    public ResponseEntity<NetBookingResponse> book(
            @AuthenticationPrincipal AcademyUserDetails user, @Valid @RequestBody NetBookingRequest request) {
        return ResponseEntity.ok(netBookingService.create(request, user));
    }

    @GetMapping("/bookings/mine")
    public ResponseEntity<List<NetBookingResponse>> myBookings(@AuthenticationPrincipal AcademyUserDetails user) {
        return ResponseEntity.ok(netBookingService.listMine(user));
    }

    @GetMapping("/fixtures")
    public ResponseEntity<List<MatchFixtureResponse>> fixtures(@AuthenticationPrincipal AcademyUserDetails user) {
        return ResponseEntity.ok(matchFixtureApplicationService.listVisible(user));
    }

    @PostMapping("/fixtures/{id}/go-live")
    public ResponseEntity<MatchFixtureResponse> goLive(
            @AuthenticationPrincipal AcademyUserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(matchFixtureApplicationService.goLive(id, user));
    }

    @PostMapping("/fixtures/{id}/complete")
    public ResponseEntity<MatchFixtureResponse> complete(
            @AuthenticationPrincipal AcademyUserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(matchFixtureApplicationService.complete(id, user));
    }

    @PostMapping("/fixtures/{id}/cancel")
    public ResponseEntity<MatchFixtureResponse> cancel(
            @AuthenticationPrincipal AcademyUserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(matchFixtureApplicationService.cancel(id, user));
    }
}
