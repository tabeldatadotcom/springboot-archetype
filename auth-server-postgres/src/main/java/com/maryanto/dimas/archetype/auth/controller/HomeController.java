package com.maryanto.dimas.archetype.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/home")
public class HomeController {

    @GetMapping("/default")
    public String home() {
        return "/home";
    }
}
