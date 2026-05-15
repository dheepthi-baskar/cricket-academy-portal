package com.cricketacademy.service;

import com.cricketacademy.domain.Batch;
import com.cricketacademy.domain.Player;
import com.cricketacademy.domain.Role;
import com.cricketacademy.domain.fixture.MatchFixture;
import com.cricketacademy.domain.fixture.MatchLifecycleState;
import com.cricketacademy.repo.BatchRepository;
import com.cricketacademy.repo.MatchFixtureRepository;
import com.cricketacademy.repo.PlayerRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.web.dto.MatchFixtureResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchFixtureApplicationService {

    private final MatchFixtureRepository fixtures;
    private final BatchRepository batches;
    private final PlayerRepository players;
    private final NotificationService notifications;

    public MatchFixtureApplicationService(
            MatchFixtureRepository fixtures,
            BatchRepository batches,
            PlayerRepository players,
            NotificationService notifications) {
        this.fixtures = fixtures;
        this.batches = batches;
        this.players = players;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<MatchFixtureResponse> listVisible(AcademyUserDetails actor) {
        return switch (actor.getRole()) {
            case ADMIN -> fixtures.findAll().stream().map(this::toResponse).toList();
            case COACH -> {
                List<Long> batchIds = batches.findByCoach_Id(actor.getId()).stream().map(Batch::getId).toList();
                if (batchIds.isEmpty()) {
                    yield List.of();
                }
                yield fixtures.findByBatch_IdIn(batchIds).stream().map(this::toResponse).toList();
            }
            case PLAYER -> {
                Player me = players.findByUser_EmailIgnoreCase(actor.getUsername()).orElseThrow();
                Long batchId = me.getBatch().getId();
                yield fixtures.findByBatch_Id(batchId).stream().map(this::toResponse).toList();
            }
        };
    }

    @Transactional
    public MatchFixtureResponse goLive(Long fixtureId, AcademyUserDetails actor) {
        return transition(fixtureId, actor, state -> state.toLive(), "Fixture is now LIVE");
    }

    @Transactional
    public MatchFixtureResponse complete(Long fixtureId, AcademyUserDetails actor) {
        return transition(fixtureId, actor, state -> state.toCompleted(), "Fixture marked COMPLETED");
    }

    @Transactional
    public MatchFixtureResponse cancel(Long fixtureId, AcademyUserDetails actor) {
        return transition(fixtureId, actor, state -> state.toCancelled(), "Fixture CANCELLED");
    }

    private MatchFixtureResponse transition(
            Long fixtureId,
            AcademyUserDetails actor,
            UnaryOperator<MatchLifecycleState> op,
            String notificationSubject) {
        MatchFixture fixture = fixtures.findByIdForUpdate(fixtureId).orElseThrow();
        assertCanManage(fixture, actor);
        var next = op.apply(fixture.lifecycle());
        fixture.apply(next);
        fixtures.save(fixture);
        if (fixture.getBatch() != null) {
            List<String> emails = new ArrayList<>(players.findEmailsByBatchId(fixture.getBatch().getId()));
            String body = fixture.getTitle() + " @ " + fixture.getScheduledAt();
            notifications.notifyRecipients(emails, notificationSubject, body);
        }
        return toResponse(fixture);
    }

    private void assertCanManage(MatchFixture fixture, AcademyUserDetails actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() == Role.COACH) {
            if (fixture.getBatch() == null) {
                throw new AccessDeniedException("Coach cannot manage fixtures without a batch");
            }
            if (!fixture.getBatch().getCoach().getId().equals(actor.getId())) {
                throw new AccessDeniedException("Coach is not assigned to this batch");
            }
            return;
        }
        throw new AccessDeniedException("Players cannot transition fixtures");
    }

    private MatchFixtureResponse toResponse(MatchFixture fx) {
        Long batchId = fx.getBatch() == null ? null : fx.getBatch().getId();
        return new MatchFixtureResponse(fx.getId(), fx.getTitle(), batchId, fx.getScheduledAt(), fx.getStatus());
    }
}
