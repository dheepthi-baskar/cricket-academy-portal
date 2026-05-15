package com.cricketacademy.domain.fixture;

public final class ScheduledFixtureState implements MatchLifecycleState {

    @Override
    public MatchFixtureStatus status() {
        return MatchFixtureStatus.SCHEDULED;
    }

    @Override
    public MatchLifecycleState toLive() {
        return new LiveFixtureState();
    }

    @Override
    public MatchLifecycleState toCompleted() {
        throw new IllegalStateException("Cannot complete a fixture that has not started");
    }

    @Override
    public MatchLifecycleState toCancelled() {
        return new CancelledFixtureState();
    }
}
