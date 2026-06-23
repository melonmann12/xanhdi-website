package com.xanhdi.website.controller;

import com.xanhdi.website.model.Tour;
import com.xanhdi.website.model.TourGuide;
import com.xanhdi.website.repository.TourRepository;
import com.xanhdi.website.repository.TourGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class GuideController {

    private final TourGuideRepository tourGuideRepository;
    private final TourRepository tourRepository;

    @Autowired
    public GuideController(TourGuideRepository tourGuideRepository, TourRepository tourRepository) {
        this.tourGuideRepository = tourGuideRepository;
        this.tourRepository = tourRepository;
    }

    @GetMapping("/guides")
    public String listGuides(Model model) {
        List<TourGuide> guides = tourGuideRepository.findAll();
        model.addAttribute("guides", guides);
        return "tourguide-list";
    }

    @GetMapping("/guides/{id}")
    public String showGuideDetail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<TourGuide> guideOpt = tourGuideRepository.findById(id);
        if (guideOpt.isEmpty()) {
            ra.addFlashAttribute("guideError", "Không tìm thấy hướng dẫn viên yêu cầu.");
            return "redirect:/guides";
        }
        
        TourGuide guide = guideOpt.get();
        List<Tour> assignedTours = tourRepository.findByGuideName(guide.getName());
        
        model.addAttribute("guide", guide);
        model.addAttribute("assignedTours", assignedTours);
        return "tourguide-detail";
    }
}
