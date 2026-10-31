package edu.usi.backend;


/*
 * WeatherService.java
 *
 * Retrieves weather forecast data from the National Weather
 * Service API and organizes it into seven daily summaries.
 * Also retrieves detailed daytime and nighttime forecasts
 * for a selected date using shared NWS request logic.
 *
 * References:
 * National Weather Service API Documentation:
 * https://www.weather.gov/documentation/services-web-api
 *
 * Spring Framework RestClient Documentation:
 * https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with the implementation
 * of the NWS API requests, processing forecast periods into
 * daily summaries, and retrieving detailed forecasts.
 */

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

// NWS connections
import java.util.Map;
import org.springframework.web.client.RestClient;

// Daily forecast imports
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherService { 
    
    // Configures communication with NWS
    private final RestClient client = RestClient.builder()
        .defaultHeader("User-Agent", "USIWeatherApp/1.0 (hpmoffatt@eagles.usi.edu)")
        .defaultHeader("Accept", "application/geo+json")
        .build();
    
    public Object getSevenDayForecast() {
        // handles the API requests
        List<?> periods = getForecastPeriods();

        // Store each day's weather information
        Map<String, Map<String, Object>> days = new LinkedHashMap<>();

        for (Object item : periods) {
            Map<?, ?> period = (Map<?, ?>) item;

            // Get the date from the forecast period
            String date = ((String) period.get("startTime")).substring(0, 10);

            Map<String, Object> day = days.get(date);

            // Create a new entry when we encounter a new date
            if (day == null) {
                if (days.size() == 7) {
                    break;
                }

                day = new LinkedHashMap<>();
                day.put("date", date);
                day.put("high", null);
                day.put("low", null);
                day.put("conditions", null);
                day.put("icon", null);
                day.put("precipitationChance", null);

                days.put(date, day);
            }

            // Store daytime weather information
            if (Boolean.TRUE.equals(period.get("isDaytime"))) {
                day.put("high", period.get("temperature"));
                day.put("conditions", period.get("shortForecast"));
                day.put("icon", period.get("icon"));
            }
            // Store nighttime weather information
            else {
                day.put("low", period.get("temperature"));

                // Use nighttime conditions if daytime is unavailable
                if (day.get("conditions") == null) {
                    day.put("conditions", period.get("shortForecast"));
                    day.put("icon", period.get("icon"));
                }
            }

            // Keep the highest precipitation chance for the day
            Map<?, ?> precipitation =
                (Map<?, ?>) period.get("probabilityOfPrecipitation");

            if (precipitation != null && precipitation.get("value") instanceof Number) {

                int chance = ((Number) precipitation.get("value")).intValue();

                Number previous = (Number) day.get("precipitationChance");

                if (previous == null || chance > previous.intValue()) {
                    day.put("precipitationChance", chance);
                }
            }
        }

        // Returns the seven day forecast
        return new ArrayList<>(days.values());
    }

    // Retrieves detailed forecast information for a selected date
    public Map<String, Object> getDailyForecast(LocalDate date) {

        Map<String, Object> day = new LinkedHashMap<>();
        day.put("date", date.toString());
        day.put("daytime", null);
        day.put("nighttime", null);

        // Checks the requested date has an available forecast
        boolean found = false;

        // Check each NWS forecast period
        for (Object item : getForecastPeriods()) {

            Map<?, ?> period = (Map<?, ?>) item;

            // Get the date associated with this forecast period
            String periodDate =
                ((String) period.get("startTime")).substring(0, 10);

            // Skip periods that do not match the requested date
            if (!periodDate.equals(date.toString())) {
                continue;
            }

            found = true;

            // Retrieve detailed information for this period
            Map<String, Object> details = new LinkedHashMap<>();

            details.put("temperature", period.get("temperature"));
            details.put("temperatureUnit", period.get("temperatureUnit"));
            details.put("shortForecast", period.get("shortForecast"));
            details.put("detailedForecast", period.get("detailedForecast"));
            details.put("windSpeed", period.get("windSpeed"));
            details.put("windDirection", period.get("windDirection"));
            details.put("icon", period.get("icon"));

            // Retrieve precipitation probability
            Map<?, ?> precipitation =
                (Map<?, ?>) period.get("probabilityOfPrecipitation");

            details.put("precipitationChance",
                precipitation == null ? null : precipitation.get("value"));

            // Separate daytime and nighttime forecasts
            if (Boolean.TRUE.equals(period.get("isDaytime"))) {
                day.put("daytime", details);
            } else {
                day.put("nighttime", details);
            }
        }

        // Indicate when the requested date is unavailable
        if (!found) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No forecast available for " + date
            );
        }

        return day;
    }

    // Retrieves forecast periods from the NWS API
    private List<?> getForecastPeriods() {

        // Get location information for USI
        Map<?, ?> location = client.get()
            .uri("https://api.weather.gov/points/37.9625,-87.6775")
            .retrieve()
            .body(Map.class);

        Map<?, ?> properties =
            (Map<?, ?>) location.get("properties");

        // Get the forecast URL
        String forecastUrl =
            (String) properties.get("forecast");

        // Request the forecast
        Map<?, ?> forecast = client.get()
            .uri(forecastUrl)
            .retrieve()
            .body(Map.class);

        Map<?, ?> forecastData =
            (Map<?, ?>) forecast.get("properties");

        return (List<?>) forecastData.get("periods");
    }
}