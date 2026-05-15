package com.cricketacademy.domain.fixture;

public final class CancelledFixtureState implements MatchLifecycleState {

    @Override
    public MatchFixtureStatus status() {
        return MatchFixtureStatus.CANCELLED;
    }

    @Override
    public MatchLifecycleState toLive() {
        throw new IllegalStateException("Terminal state: cancelled");
    }

    @Override
    public MatchLifecycleState toCompleted() {
        throw new IllegalStateException("Cannot complete a cancelled fixture");
    }

    @Override
    public MatchLifecycleState toCancelled() {
        throw new IllegalStateException("Fixture is already cancelled");
    }
}
