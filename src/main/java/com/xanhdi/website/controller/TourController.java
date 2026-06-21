package com.xanhdi.website.controller;

import com.xanhdi.website.model.Tour;
import com.xanhdi.website.model.TourTimeline;
import com.xanhdi.website.repository.TourRepository;
import com.xanhdi.website.repository.TourTimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TourController {

    private final TourRepository tourRepository;
    private final TourTimelineRepository tourTimelineRepository;

    @Autowired
    public TourController(TourRepository tourRepository, TourTimelineRepository tourTimelineRepository) {
        this.tourRepository = tourRepository;
        this.tourTimelineRepository = tourTimelineRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        return "index";
    }

    @GetMapping("/tours")
    public String tours(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        return "tour-list";
    }

    @GetMapping("/tour-detail/{id}")
    public String tourDetail(@PathVariable("id") Long id, Model model) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tour Id:" + id));
        model.addAttribute("tour", tour);
        model.addAttribute("reviews", tour.getReviews());
        return "tour-detail";
    }

    @GetMapping("/tour-detail/{id}/timeline")
    public String tourTimelineDetail(@PathVariable("id") Long id, Model model) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tour Id:" + id));
        List<TourTimeline> timelines = tourTimelineRepository.findByTourIdOrderBySortOrderAsc(id);
        model.addAttribute("tour", tour);
        model.addAttribute("timelines", timelines);
        return "tour-timeline-detail";
    }
}
