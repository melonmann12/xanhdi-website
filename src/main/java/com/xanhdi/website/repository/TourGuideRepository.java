package com.xanhdi.website.repository;

import com.xanhdi.website.model.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    List<TourGuide> findTop3ByOrderByRatingDesc();
}
