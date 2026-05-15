package com.cricketacademy.repo;

import com.cricketacademy.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {}
