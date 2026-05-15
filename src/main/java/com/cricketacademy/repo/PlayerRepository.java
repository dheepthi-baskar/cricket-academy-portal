package com.cricketacademy.repo;

import com.cricketacademy.domain.Player;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUser_EmailIgnoreCase(String email);

    @Query(
            "select p from Player p join fetch p.batch b join fetch b.coach join fetch p.user where p.id = :id")
    Optional<Player> findByIdForAccess(@Param("id") Long id);

    @Query("select p.user.email from Player p where p.batch.id = :batchId")
    List<String> findEmailsByBatchId(@Param("batchId") Long batchId);

    @Query("select p from Player p join fetch p.batch order by p.displayName")
    List<Player> findAllWithBatch();
}
