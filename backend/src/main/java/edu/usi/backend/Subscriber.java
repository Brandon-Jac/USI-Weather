
/*
 * Subscriber.java
 *
 * Represents a user subscribed to email notifications
 * for severe weather alerts and campus construction
 * updates. Stores email addresses, notification
 * preferences, verification status, and registration
 * timestamps in the PostgreSQL database.
 *
 * References:
 * Jakarta Persistence Documentation:
 * https://jakarta.ee/specifications/persistence/
 *
 * Spring Data JPA Documentation:
 * https://docs.spring.io/spring-data/jpa/reference/
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with configuring database
 * mappings and managing registration timestamps.
 */


package edu.usi.backend;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// Database entity
@Entity
@Table(name = "subscribers")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean weatherAlerts;

    @Column(nullable = false)
    private boolean constructionAlerts;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Required by JPA
    protected Subscriber() {}

    // Creates a new subscriber
    public Subscriber(String email, boolean weatherAlerts, boolean constructionAlerts) {
        this.email = email;
        this.weatherAlerts = weatherAlerts;
        this.constructionAlerts = constructionAlerts;
    }

    // Records when the subscriber was created
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
