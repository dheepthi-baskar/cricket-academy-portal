package com.cricketacademy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "performance_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(nullable = false)
    private LocalDate metricDate;

    /** km/h or mph depending on academy convention; stored as numeric value */
    @Column
    private Double bowlingSpeed;

    @Column
    private Integer runsScored;

    @Column
    private Integer wicketsTaken;

    /** 1-10 inclusive */
    @Column
    private Integer fitnessScore;

    @Column(length = 2000)
    private String coachNotes;
}
