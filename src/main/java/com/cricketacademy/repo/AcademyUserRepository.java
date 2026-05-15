package com.cricketacademy.repo;

import com.cricketacademy.domain.AcademyUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademyUserRepository extends JpaRepository<AcademyUser, Long> {

    Optional<AcademyUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
