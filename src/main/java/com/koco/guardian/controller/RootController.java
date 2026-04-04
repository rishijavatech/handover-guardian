package com.koco.guardian.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Root")
public class RootController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("service", "guardian-service", "status", "ok");
    }
}
