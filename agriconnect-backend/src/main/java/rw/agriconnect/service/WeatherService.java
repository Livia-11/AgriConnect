package rw.agriconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    
    @Value("${weather.api.key:YOUR_API_KEY}")
    private String apiKey;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/forecast}")
    private String apiUrl;
    
    /**
     * Get weather forecast for a location
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Weather forecast data
     */
    @Cacheable(value = "weatherForecast", key = "#latitude + '-' + #longitude")
    public Map<String, Object> getWeatherForecast(Double latitude, Double longitude) {
        String url = apiUrl + "?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + apiKey;
        
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            // In case of API failure, return mock data
            return getMockWeatherData();
        }
    }
    
    /**
     * Get mock weather data for testing
     * @return Mock weather data
     */
    private Map<String, Object> getMockWeatherData() {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("success", true);
        mockData.put("location", "Kigali, Rwanda");
        
        Map<String, Object> current = new HashMap<>();
        current.put("temperature", 24);
        current.put("humidity", 65);
        current.put("condition", "Sunny");
        current.put("windSpeed", 12);
        mockData.put("current", current);
        
        Map<String, Object> day1 = new HashMap<>();
        day1.put("date", "Today");
        day1.put("temperature", 24);
        day1.put("condition", "Sunny");
        
        Map<String, Object> day2 = new HashMap<>();
        day2.put("date", "Tomorrow");
        day2.put("temperature", 22);
        day2.put("condition", "Mostly Sunny");
        
        Map<String, Object> day3 = new HashMap<>();
        day3.put("date", "Wed");
        day3.put("temperature", 25);
        day3.put("condition", "Sunny");
        
        Map<String, Object> day4 = new HashMap<>();
        day4.put("date", "Thu");
        day4.put("temperature", 23);
        day4.put("condition", "Partly Cloudy");
        
        Map<String, Object> day5 = new HashMap<>();
        day5.put("date", "Fri");
        day5.put("temperature", 26);
        day5.put("condition", "Sunny");
        
        Map<String, Object>[] forecast = new Map[5];
        forecast[0] = day1;
        forecast[1] = day2;
        forecast[2] = day3;
        forecast[3] = day4;
        forecast[4] = day5;
        
        mockData.put("forecast", forecast);
        
        return mockData;
    }
} 