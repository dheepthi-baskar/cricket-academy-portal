package com.cricketacademy.domain.fixture;

public interface MatchLifecycleState {

    MatchFixtureStatus status();

    MatchLifecycleState toLive();

    MatchLifecycleState toCompleted();

    MatchLifecycleState toCancelled();
}
