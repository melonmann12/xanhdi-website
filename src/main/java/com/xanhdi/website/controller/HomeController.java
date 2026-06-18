package com.xanhdi.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/tours")
    public String tours() {
        return "tour-list";
    }

    @GetMapping("/tour-detail")
    public String tourDetail() {
        return "tour-detail";
    }
}
