package com.cricketacademy.service;

import com.cricketacademy.domain.PerformanceMetric;
import com.cricketacademy.domain.Player;
import com.cricketacademy.domain.Role;
import com.cricketacademy.repo.PerformanceMetricRepository;
import com.cricketacademy.repo.PlayerRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.web.dto.PerformanceMetricRequest;
import com.cricketacademy.web.dto.PerformanceMetricResponse;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceCommandService {

    private final PerformanceMetricRepository metrics;
    private final PlayerRepository players;

    public PerformanceCommandService(PerformanceMetricRepository metrics, PlayerRepository players) {
        this.metrics = metrics;
        this.players = players;
    }

    @Transactional(readOnly = true)
    public List<PerformanceMetricResponse> listForPlayer(Long playerId, AcademyUserDetails actor) {
        assertCanViewPlayer(playerId, actor);
        return metrics.findByPlayerIdOrderByMetricDateDesc(playerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PerformanceMetricResponse recordMetric(Long playerId, PerformanceMetricRequest request, AcademyUserDetails actor) {
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.COACH) {
            throw new AccessDeniedException("Only coaches or admins can record metrics");
        }
        Player player = players.findByIdForAccess(playerId).orElseThrow();
        if (actor.getRole() == Role.COACH && !player.getBatch().getCoach().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Coach is not assigned to this player's batch");
        }
        PerformanceMetric entity = PerformanceMetric.builder()
                .player(player)
                .metricDate(request.metricDate())
                .bowlingSpeed(request.bowlingSpeed())
                .runsScored(request.runsScored())
                .wicketsTaken(request.wicketsTaken())
                .fitnessScore(request.fitnessScore())
                .coachNotes(request.coachNotes())
                .build();
        return toResponse(metrics.save(entity));
    }

    private void assertCanViewPlayer(Long playerId, AcademyUserDetails actor) {
        Player player = players.findByIdForAccess(playerId).orElseThrow();
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() == Role.COACH) {
            if (!player.getBatch().getCoach().getId().equals(actor.getId())) {
                throw new AccessDeniedException("Coach cannot view this player");
            }
            return;
        }
        if (actor.getRole() == Role.PLAYER) {
            if (!player.getUser().getId().equals(actor.getId())) {
                throw new AccessDeniedException("Players can only view their own metrics");
            }
            return;
        }
        throw new AccessDeniedException("Unsupported role");
    }

    private PerformanceMetricResponse toResponse(PerformanceMetric m) {
        return new PerformanceMetricResponse(
                m.getId(),
                m.getPlayer().getId(),
                m.getMetricDate(),
                m.getBowlingSpeed(),
                m.getRunsScored(),
                m.getWicketsTaken(),
                m.getFitnessScore(),
                m.getCoachNotes());
    }
}
