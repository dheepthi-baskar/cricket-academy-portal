package com.cricketacademy.service;

import com.cricketacademy.repo.BillingStatementRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.web.dto.BillingStatementResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoachBillingService {

    private final BillingStatementRepository billing;

    public CoachBillingService(BillingStatementRepository billing) {
        this.billing = billing;
    }

    @Transactional(readOnly = true)
    public List<BillingStatementResponse> summariesForCoach(AcademyUserDetails coach) {
        return billing.findByPlayer_Batch_Coach_Id(coach.getId()).stream()
                .map(b -> new BillingStatementResponse(
                        b.getId(),
                        b.getPlayer().getId(),
                        b.getPlayer().getDisplayName(),
                        b.getPeriodLabel(),
                        b.getAmountDue()))
                .toList();
    }
}
