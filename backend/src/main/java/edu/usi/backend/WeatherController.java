package edu.usi.backend;

/*
 * WeatherController.java
 *
 * Handles HTTP requests for weather information and calls
 * WeatherService to retrieve the requested data.
 *
 * References:
 * Spring Framework MVC Controller Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
 *
 * Spring Framework Request Mapping Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with the controller
 * dependency injection and REST endpoint setup.
 */

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
// CORS import
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
// Base URL
@RequestMapping("/api/weather")
// Allows the Angular frontend to access the backend
@CrossOrigin(origins = "http://localhost:4200")

public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Creates the 7-day-forecast endpoint
    @GetMapping("/7-day")
    public Object getSevenDayForecast() {
        // Retrieves the forecast from WeatherService.java
        return weatherService.getSevenDayForecast();
    }

    // Creates the daily forecast endpoint
    @GetMapping("/daily/{date}")
    public Map<String, Object> getDailyForecast(
        @PathVariable("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return weatherService.getDailyForecast(date);
    }
}
