package edu.max.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @GetMapping("/login")
    public String getLogInPage() {
        return "login";
    }
}