package com.cricketacademy.web;

import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.service.AdminPortalService;
import com.cricketacademy.web.dto.BatchResponse;
import com.cricketacademy.web.dto.CreateBatchRequest;
import com.cricketacademy.web.dto.CreateFacilityRequest;
import com.cricketacademy.web.dto.CreateFixtureRequest;
import com.cricketacademy.web.dto.FacilityResponse;
import com.cricketacademy.web.dto.MatchFixtureResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminPortalService adminPortalService;

    public AdminController(AdminPortalService adminPortalService) {
        this.adminPortalService = adminPortalService;
    }

    @GetMapping("/batches")
    public ResponseEntity<List<BatchResponse>> batches(@AuthenticationPrincipal AcademyUserDetails user) {
        return ResponseEntity.ok(adminPortalService.listBatches());
    }

    @PostMapping("/batches")
    public ResponseEntity<BatchResponse> createBatch(
            @AuthenticationPrincipal AcademyUserDetails user, @Valid @RequestBody CreateBatchRequest request) {
        return ResponseEntity.ok(adminPortalService.createBatch(request, user));
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<FacilityResponse>> facilities() {
        return ResponseEntity.ok(adminPortalService.listFacilities().stream()
                .map(f -> new FacilityResponse(f.getId(), f.getName(), f.getDescription()))
                .toList());
    }

    @PostMapping("/facilities")
    public ResponseEntity<FacilityResponse> createFacility(
            @AuthenticationPrincipal AcademyUserDetails user, @Valid @RequestBody CreateFacilityRequest request) {
        var f = adminPortalService.createFacility(request.name(), request.description(), user);
        return ResponseEntity.ok(new FacilityResponse(f.getId(), f.getName(), f.getDescription()));
    }

    @PostMapping("/fixtures")
    public ResponseEntity<MatchFixtureResponse> createFixture(
            @AuthenticationPrincipal AcademyUserDetails user, @Valid @RequestBody CreateFixtureRequest request) {
        return ResponseEntity.ok(adminPortalService.createFixture(
                request.title(), request.batchId(), request.scheduledAt(), user));
    }
}
