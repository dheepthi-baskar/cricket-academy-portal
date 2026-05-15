package com.cricketacademy.service;

import com.cricketacademy.domain.AcademyUser;
import com.cricketacademy.domain.Batch;
import com.cricketacademy.domain.Facility;
import com.cricketacademy.domain.Role;
import com.cricketacademy.domain.fixture.MatchFixture;
import com.cricketacademy.domain.fixture.MatchFixtureStatus;
import com.cricketacademy.repo.AcademyUserRepository;
import com.cricketacademy.repo.BatchRepository;
import com.cricketacademy.repo.FacilityRepository;
import com.cricketacademy.repo.MatchFixtureRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.web.dto.BatchResponse;
import com.cricketacademy.web.dto.CreateBatchRequest;
import com.cricketacademy.web.dto.MatchFixtureResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminPortalService {

    private final BatchRepository batches;
    private final AcademyUserRepository users;
    private final FacilityRepository facilities;
    private final MatchFixtureRepository fixtures;

    public AdminPortalService(
            BatchRepository batches,
            AcademyUserRepository users,
            FacilityRepository facilities,
            MatchFixtureRepository fixtures) {
        this.batches = batches;
        this.users = users;
        this.facilities = facilities;
        this.fixtures = fixtures;
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> listBatches() {
        return batches.findAll().stream().map(this::toBatchResponse).toList();
    }

    @Transactional
    public BatchResponse createBatch(CreateBatchRequest request, AcademyUserDetails actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin only");
        }
        AcademyUser coach = users.findById(request.coachUserId()).orElseThrow();
        if (coach.getRole() != Role.COACH && coach.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Assigned user must be a coach or admin");
        }
        Batch batch = Batch.builder()
                .name(request.name())
                .description(request.description())
                .coach(coach)
                .build();
        batch = batches.save(batch);
        return toBatchResponse(batch);
    }

    @Transactional
    public Facility createFacility(String name, String description, AcademyUserDetails actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin only");
        }
        return facilities.save(Facility.builder().name(name).description(description).build());
    }

    @Transactional(readOnly = true)
    public List<Facility> listFacilities() {
        return facilities.findAll();
    }

    @Transactional
    public MatchFixtureResponse createFixture(String title, Long batchId, Instant scheduledAt, AcademyUserDetails actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin only");
        }
        Batch batch = batches.findById(batchId).orElseThrow();
        MatchFixture fixture = MatchFixture.builder()
                .title(title)
                .batch(batch)
                .scheduledAt(scheduledAt)
                .status(MatchFixtureStatus.SCHEDULED)
                .build();
        fixture = fixtures.save(fixture);
        return toFixture(fixture);
    }

    private BatchResponse toBatchResponse(Batch batch) {
        return new BatchResponse(
                batch.getId(),
                batch.getName(),
                batch.getDescription(),
                batch.getCoach().getId(),
                batch.getCoach().getFullName());
    }

    private MatchFixtureResponse toFixture(MatchFixture fx) {
        Long batchId = fx.getBatch() == null ? null : fx.getBatch().getId();
        return new MatchFixtureResponse(fx.getId(), fx.getTitle(), batchId, fx.getScheduledAt(), fx.getStatus());
    }
}
