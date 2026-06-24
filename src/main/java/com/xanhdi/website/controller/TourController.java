package com.xanhdi.website.controller;

import com.xanhdi.website.model.Tour;
import com.xanhdi.website.model.TourTimeline;
import com.xanhdi.website.model.TourGuide;
import com.xanhdi.website.repository.TourRepository;
import com.xanhdi.website.repository.TourTimelineRepository;
import com.xanhdi.website.repository.TourGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.xanhdi.website.service.SystemSettingService;
import com.xanhdi.website.model.SystemSetting;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TourController {

    private final TourRepository tourRepository;
    private final TourTimelineRepository tourTimelineRepository;
    private final TourGuideRepository tourGuideRepository;
    private final SystemSettingService systemSettingService;

    @Autowired
    public TourController(TourRepository tourRepository, 
                          TourTimelineRepository tourTimelineRepository,
                          TourGuideRepository tourGuideRepository,
                          SystemSettingService systemSettingService) {
        this.tourRepository = tourRepository;
        this.tourTimelineRepository = tourTimelineRepository;
        this.tourGuideRepository = tourGuideRepository;
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        model.addAttribute("featuredGuides", tourGuideRepository.findTop3ByOrderByRatingDesc());
        
        Map<String, String> settings = systemSettingService.getSettingsMap();
        model.addAttribute("settings", settings);
        
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
