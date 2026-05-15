package com.cricketacademy.bootstrap;

import com.cricketacademy.domain.AcademyUser;
import com.cricketacademy.domain.Batch;
import com.cricketacademy.domain.BillingStatement;
import com.cricketacademy.domain.Facility;
import com.cricketacademy.domain.PerformanceMetric;
import com.cricketacademy.domain.Player;
import com.cricketacademy.domain.Role;
import com.cricketacademy.domain.fixture.MatchFixture;
import com.cricketacademy.domain.fixture.MatchFixtureStatus;
import com.cricketacademy.repo.AcademyUserRepository;
import com.cricketacademy.repo.BatchRepository;
import com.cricketacademy.repo.BillingStatementRepository;
import com.cricketacademy.repo.FacilityRepository;
import com.cricketacademy.repo.MatchFixtureRepository;
import com.cricketacademy.repo.PerformanceMetricRepository;
import com.cricketacademy.repo.PlayerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AcademyDataSeeder implements CommandLineRunner {

    private final AcademyUserRepository users;
    private final BatchRepository batches;
    private final PlayerRepository players;
    private final FacilityRepository facilities;
    private final PerformanceMetricRepository metrics;
    private final BillingStatementRepository billing;
    private final MatchFixtureRepository fixtures;
    private final PasswordEncoder passwordEncoder;

    public AcademyDataSeeder(
            AcademyUserRepository users,
            BatchRepository batches,
            PlayerRepository players,
            FacilityRepository facilities,
            PerformanceMetricRepository metrics,
            BillingStatementRepository billing,
            MatchFixtureRepository fixtures,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.batches = batches;
        this.players = players;
        this.facilities = facilities;
        this.metrics = metrics;
        this.billing = billing;
        this.fixtures = fixtures;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }

        users.save(AcademyUser.builder()
                .email("admin@academy.local")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("System Admin")
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        AcademyUser coach = users.save(AcademyUser.builder()
                .email("coach@academy.local")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Head Coach")
                .role(Role.COACH)
                .enabled(true)
                .build());

        AcademyUser playerUser = users.save(AcademyUser.builder()
                .email("player@academy.local")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Aspiring Batter")
                .role(Role.PLAYER)
                .enabled(true)
                .build());

        Batch elite = batches.save(Batch.builder()
                .name("Under-19 Elite")
                .description("High performance cohort")
                .coach(coach)
                .build());

        Player player = players.save(Player.builder()
                .user(playerUser)
                .batch(elite)
                .displayName("Aspiring Batter")
                .build());

        facilities.save(Facility.builder()
                .name("Centre Indoor Nets")
                .description("Six-lane facility with bowling machine bay")
                .build());

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 5; i++) {
            LocalDate day = today.minusDays(i * 3L);
            metrics.save(PerformanceMetric.builder()
                    .player(player)
                    .metricDate(day)
                    .bowlingSpeed(120.0 + i)
                    .runsScored(20 + i * 5)
                    .wicketsTaken(i % 3)
                    .fitnessScore(6 + (i % 4))
                    .coachNotes("Solid intent vs spin on day " + i)
                    .build());
        }

        billing.save(BillingStatement.builder()
                .player(player)
                .periodLabel(today.getMonth().name() + " " + today.getYear())
                .amountDue(new BigDecimal("249.00"))
                .notes("Academy membership")
                .build());

        fixtures.save(MatchFixture.builder()
                .title("Internal T20 Practice Match")
                .batch(elite)
                .scheduledAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .status(MatchFixtureStatus.SCHEDULED)
                .build());
    }
}
