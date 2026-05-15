package com.cricketacademy.repo;

import com.cricketacademy.domain.NetBooking;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NetBookingRepository extends JpaRepository<NetBooking, Long> {

    List<NetBooking> findByBookedBy_IdOrderByStartTimeDesc(Long userId);

    @Query(
            """
            select case when count(b) > 0 then true else false end
            from NetBooking b
            where b.facility.id = :facilityId
              and b.startTime < :end
              and b.endTime > :start
            """)
    boolean existsOverlap(
            @Param("facilityId") Long facilityId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query(
            """
            select case when count(b) > 0 then true else false end
            from NetBooking b
            where b.facility.id = :facilityId
              and b.id <> :excludeId
              and b.startTime < :end
              and b.endTime > :start
            """)
    boolean existsOverlapExcluding(
            @Param("facilityId") Long facilityId,
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("excludeId") Long excludeId);
}
