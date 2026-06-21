package com.xanhdi.website.repository;

import com.xanhdi.website.model.TourTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourTimelineRepository extends JpaRepository<TourTimeline, Long> {
    List<TourTimeline> findByTourIdOrderBySortOrderAsc(Long tourId);
}
