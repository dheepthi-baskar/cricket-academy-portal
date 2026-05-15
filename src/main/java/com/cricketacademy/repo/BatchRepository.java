package com.cricketacademy.repo;

import com.cricketacademy.domain.Batch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByCoach_Id(Long coachUserId);
}
