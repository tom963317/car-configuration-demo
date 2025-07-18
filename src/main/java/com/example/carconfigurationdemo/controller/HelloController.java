package com.example.carconfigurationdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("/versiondata")
    public String versionData() {
        return "versionlist";
    }
}
