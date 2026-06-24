package com.xanhdi.website.repository;

import com.xanhdi.website.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTourIdOrderByCreatedAtDesc(Long tourId);

    @EntityGraph(attributePaths = {"tour"})
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdWithTour(@Param("id") Long id);

    List<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
