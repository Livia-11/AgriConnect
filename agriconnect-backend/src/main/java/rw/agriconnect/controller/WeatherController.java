package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.agriconnect.service.WeatherService;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    
    @GetMapping("/forecast")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<Map<String, Object>> getWeatherForecast(
            @RequestParam(defaultValue = "-1.9441") Double latitude,
            @RequestParam(defaultValue = "30.0619") Double longitude) {
        // Default coordinates are for Kigali, Rwanda if not provided
        Map<String, Object> forecast = weatherService.getWeatherForecast(latitude, longitude);
        return ResponseEntity.ok(forecast);
    }
} 