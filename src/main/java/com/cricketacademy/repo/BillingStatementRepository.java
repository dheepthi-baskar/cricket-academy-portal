package com.cricketacademy.repo;

import com.cricketacademy.domain.BillingStatement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingStatementRepository extends JpaRepository<BillingStatement, Long> {

    List<BillingStatement> findByPlayer_Batch_Coach_Id(Long coachUserId);
}
