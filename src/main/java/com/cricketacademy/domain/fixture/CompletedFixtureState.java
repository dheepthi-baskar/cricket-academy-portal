package com.cricketacademy.domain.fixture;

public final class CompletedFixtureState implements MatchLifecycleState {

    @Override
    public MatchFixtureStatus status() {
        return MatchFixtureStatus.COMPLETED;
    }

    @Override
    public MatchLifecycleState toLive() {
        throw new IllegalStateException("Terminal state: completed");
    }

    @Override
    public MatchLifecycleState toCompleted() {
        throw new IllegalStateException("Fixture is already completed");
    }

    @Override
    public MatchLifecycleState toCancelled() {
        throw new IllegalStateException("Cannot cancel a completed fixture");
    }
}
