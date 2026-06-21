package com.xanhdi.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TourController {

    @GetMapping("/tour-detail/timeline")
    public String tourTimelineDetail() {
        return "tour-timeline-detail";
    }
}
