package com.xanhdi.website.repository;

import com.xanhdi.website.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByGuideName(String guideName);
    List<Tour> findAllByOrderByIdDesc(Pageable pageable);
}
