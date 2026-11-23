
/*
 * NotificationController.java
 *
 * Handles HTTP requests for email subscription
 * registration in the USI Weather App. Receives
 * email addresses and notification preferences
 * from the Angular frontend and passes them to
 * NotificationService for processing.
 *
 * References:
 * Spring Framework MVC Controller Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
 *
 * Spring Framework Request Body Documentation:
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with dependency injection, input validation, 
 * and REST endpoint configuration.
 */


package edu.usi.backend;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Receives new email subscription requests
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {

        notificationService.subscribe(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(Map.of(
                "message",
                "Subscription request saved. Email verification is pending implementation."
            ));
    }
}
