package com.finpro.backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "FireGirl & WaterBoy Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "FireGirl & WaterBoy Backend");
        response.put("version", "1.0.0");
        response.put("game", "2-Player Puzzle Platformer");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "GET /api/health");
        endpoints.put("create_players", "POST /api/game/start");
        endpoints.put("finish_game", "PUT /api/sessions/{sessionId}/finish");
        endpoints.put("leaderboard", "GET /api/sessions/leaderboard");
        endpoints.put("players", "GET /api/players");

        response.put("endpoints", endpoints);
        return response;
    }
}