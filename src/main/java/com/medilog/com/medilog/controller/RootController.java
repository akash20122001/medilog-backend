package com.medilog.com.medilog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {
    
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Medilog API Server is running! API endpoints are available at /api";
    }
    
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}