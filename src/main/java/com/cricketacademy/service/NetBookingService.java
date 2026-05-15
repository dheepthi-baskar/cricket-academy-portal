package com.cricketacademy.service;

import com.cricketacademy.domain.AcademyUser;
import com.cricketacademy.domain.Facility;
import com.cricketacademy.domain.NetBooking;
import com.cricketacademy.domain.Role;
import com.cricketacademy.repo.AcademyUserRepository;
import com.cricketacademy.repo.FacilityRepository;
import com.cricketacademy.repo.NetBookingRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.web.dto.NetBookingRequest;
import com.cricketacademy.web.dto.NetBookingResponse;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NetBookingService {

    private final NetBookingRepository bookings;
    private final FacilityRepository facilities;
    private final AcademyUserRepository users;

    public NetBookingService(
            NetBookingRepository bookings, FacilityRepository facilities, AcademyUserRepository users) {
        this.bookings = bookings;
        this.facilities = facilities;
        this.users = users;
    }

    @Transactional
    public NetBookingResponse create(NetBookingRequest request, AcademyUserDetails actor) {
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.COACH) {
            throw new AccessDeniedException("Only coaches or admins can book nets");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        Facility facility = facilities.findById(request.facilityId()).orElseThrow();
        boolean overlap = bookings.existsOverlap(facility.getId(), request.startTime(), request.endTime());
        if (overlap) {
            throw new IllegalStateException("Facility already booked for this window");
        }
        AcademyUser booker = users.getReferenceById(actor.getId());
        NetBooking entity = NetBooking.builder()
                .facility(facility)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .bookedBy(booker)
                .notes(request.notes())
                .build();
        entity = bookings.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<NetBookingResponse> listMine(AcademyUserDetails actor) {
        return bookings.findByBookedBy_IdOrderByStartTimeDesc(actor.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private NetBookingResponse toResponse(NetBooking b) {
        return new NetBookingResponse(
                b.getId(),
                b.getFacility().getId(),
                b.getFacility().getName(),
                b.getStartTime(),
                b.getEndTime(),
                b.getNotes());
    }
}
