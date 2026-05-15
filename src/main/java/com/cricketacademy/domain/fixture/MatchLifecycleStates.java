package com.cricketacademy.domain.fixture;

public final class MatchLifecycleStates {

    private MatchLifecycleStates() {}

    public static MatchLifecycleState from(MatchFixtureStatus status) {
        return switch (status) {
            case SCHEDULED -> new ScheduledFixtureState();
            case LIVE -> new LiveFixtureState();
            case COMPLETED -> new CompletedFixtureState();
            case CANCELLED -> new CancelledFixtureState();
        };
    }
}
