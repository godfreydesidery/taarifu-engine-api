package com.taarifu_engine_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/engine")
public class EngineController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getEngineStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("engineName", "Taarifu Engine");
        response.put("status", "Running");
        response.put("version", "1.0.0");
        response.put("uptime", "0 seconds");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Request processed successfully");
        response.put("requestId", "req-" + System.currentTimeMillis());
        response.put("processedAt", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getEngineInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Taarifu Engine API");
        response.put("description", "Spring Boot application for Taarifu Engine");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("springBootVersion", "3.2.0");
        
        return ResponseEntity.ok(response);
    }
}
