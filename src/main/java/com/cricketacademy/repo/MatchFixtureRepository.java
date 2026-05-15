package com.cricketacademy.repo;

import com.cricketacademy.domain.fixture.MatchFixture;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchFixtureRepository extends JpaRepository<MatchFixture, Long> {

    List<MatchFixture> findByBatch_Id(Long batchId);

    List<MatchFixture> findByBatch_IdIn(Collection<Long> batchIds);

    @Query("select f from MatchFixture f left join fetch f.batch b left join fetch b.coach where f.id = :id")
    Optional<MatchFixture> findByIdForUpdate(@Param("id") Long id);
}
