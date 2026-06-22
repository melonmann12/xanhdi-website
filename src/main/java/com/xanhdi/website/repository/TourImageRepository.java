package com.xanhdi.website.repository;

import com.xanhdi.website.model.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long> {
    List<TourImage> findByTourId(Long tourId);
}
