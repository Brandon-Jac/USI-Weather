
/*
 * NotificationService.java
 *
 * Handles email subscription registration for the
 * USI Weather App. Processes subscription requests,
 * normalizes email addresses, validates notification
 * preferences, prevents duplicate registrations,
 * and saves new subscribers to the PostgreSQL database.
 *
 * References:
 * Spring Framework Transaction Documentation:
 * https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
 *
 * Spring Data JPA Documentation:
 * https://docs.spring.io/spring-data/jpa/reference/
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with implementing
 * subscription registration, and checking/saving subscriber information.
 */


package edu.usi.backend;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {
    private final SubscriberRepository subscriberRepository;

    public NotificationService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    // Registers new email subscriptions
    @Transactional
    public void subscribe(SubscriptionRequest request) {

        // Change the email address to lowercase for uniform formatting
        String email = request.email().trim()
            .toLowerCase(Locale.ROOT);

        boolean weatherAlerts =
            Boolean.TRUE.equals(request.weatherAlerts());

        boolean constructionAlerts =
            Boolean.TRUE.equals(request.constructionAlerts());

        // Require at least one notification preference
        if (!weatherAlerts && !constructionAlerts) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Select at least one notification type"
            );
        }

        // Do not allow duplicate subscriptions
        if (subscriberRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        // Create a subscription
        Subscriber subscriber = new Subscriber(
            email,
            weatherAlerts,
            constructionAlerts
        );

        // Save the subscriber in PostgreSQL
        subscriberRepository.save(subscriber);
    }
}
