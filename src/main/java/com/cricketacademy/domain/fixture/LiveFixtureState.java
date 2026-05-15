package com.cricketacademy.domain.fixture;

public final class LiveFixtureState implements MatchLifecycleState {

    @Override
    public MatchFixtureStatus status() {
        return MatchFixtureStatus.LIVE;
    }

    @Override
    public MatchLifecycleState toLive() {
        throw new IllegalStateException("Fixture is already live");
    }

    @Override
    public MatchLifecycleState toCompleted() {
        return new CompletedFixtureState();
    }

    @Override
    public MatchLifecycleState toCancelled() {
        return new CancelledFixtureState();
    }
}
