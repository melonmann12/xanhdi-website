package com.xanhdi.website.repository;

import com.xanhdi.website.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByUsername(String username);
}
