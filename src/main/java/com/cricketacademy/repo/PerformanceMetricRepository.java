package com.cricketacademy.repo;

import com.cricketacademy.domain.PerformanceMetric;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {

    List<PerformanceMetric> findByPlayerIdAndMetricDateBetweenOrderByMetricDateAsc(
            Long playerId, LocalDate startInclusive, LocalDate endInclusive);

    List<PerformanceMetric> findByPlayerIdOrderByMetricDateDesc(Long playerId);
}
