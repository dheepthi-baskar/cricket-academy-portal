package com.cricketacademy.security;

import com.cricketacademy.repo.AcademyUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AcademyUserDetailsService implements UserDetailsService {

    private final AcademyUserRepository users;

    public AcademyUserDetailsService(AcademyUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByEmailIgnoreCase(username)
                .map(u -> new AcademyUserDetails(
                        u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.isEnabled()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
