/*
 * WeatherAlertService.java
 *
 * Retrieves active weather alerts affecting the University
 * of Southern Indiana campus using the National Weather
 * Service (NWS) API. Processes the returned alert data
 * and organizes relevant information for frontend use.
 *
 * References:
 * National Weather Service API Documentation:
 * https://www.weather.gov/documentation/services-web-api
 *
 * Spring Framework RestClient Documentation:
 * https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with NWS API requests,
 * processing weather alert data, and API errors.
 */

package edu.usi.backend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherAlertService {
    // NWS API Communication
    private final RestClient client = RestClient.builder()
        .defaultHeader("User-Agent", "USIWeatherApp/1.0 (hpmoffatt@eagles.usi.edu)")
        .defaultHeader("Accept", "application/geo+json")
        .build();

    // Retrieve active weather alerts
    public List<Map<String, Object>> getActiveAlerts() {

        Map<?, ?> response;

        try {
            response = client.get()
                .uri("https://api.weather.gov/alerts/active?point=37.9625,-87.6775")
                .retrieve()
                .body(Map.class);
        } 
        catch (RestClientException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Unable to retrieve NWS alerts", ex);
        }

        // NWS Response Checking
        if (response == null || !(response.get("features") instanceof List<?> features)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Invalid response from NWS");
        }

        List<Map<String, Object>> alerts = new ArrayList<>();

        for (Object item : features) {

            Map<?, ?> feature = (Map<?, ?>) item;
            Map<?, ?> properties = (Map<?, ?>) feature.get("properties");

            if (properties == null) {
                continue;
            }

            // Storing Alert Information
            Map<String, Object> alert = new LinkedHashMap<>();
            alert.put("id", properties.get("id"));
            alert.put("event", properties.get("event"));
            alert.put("severity", properties.get("severity"));
            alert.put("urgency", properties.get("urgency"));
            alert.put("headline", properties.get("headline"));
            alert.put("description", properties.get("description"));
            alert.put("instruction", properties.get("instruction"));
            alert.put("areaDesc", properties.get("areaDesc"));
            alert.put("effective", properties.get("effective"));
            alert.put("expires", properties.get("expires"));
            alert.put("ends", properties.get("ends"));
        }
        
        return alerts;
    }
}
