/*
 * WeatherAlertController.java
 *
 * Handles HTTP requests for active weather alerts
 * affecting the University of Southern Indiana campus.
 * Calls WeatherAlertService to retrieve and return
 * weather alert information to the frontend.
 *
 * References:
 * Spring Framework MVC Controller Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
 *
 * Spring Framework Request Mapping Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with dependency
 * injection and REST endpoint setup.
 */

package edu.usi.backend;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("/api/weather/alerts")
@CrossOrigin(origins = "http://localhost:4200")
public class WeatherAlertController {
    private final WeatherAlertService weatherAlertService;

    public WeatherAlertController(WeatherAlertService weatherAlertService) {
        this.weatherAlertService = weatherAlertService;
    }

    // Return active weather alerts
    @GetMapping
    public List<Map<String, Object>> getActiveAlerts() {
        return weatherAlertService.getActiveAlerts();
    }
}
