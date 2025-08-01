package com.agridoc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "✅ Agridoc backend is working!";
    }

    @GetMapping("/api/test")
    public String test() {
        return "✅ Test endpoint reached!";
    }
}
