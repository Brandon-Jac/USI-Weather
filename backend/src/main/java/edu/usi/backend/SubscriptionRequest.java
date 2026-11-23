
/*
 * SubscriptionRequest.java
 *
 * Contains the email address and notification 
 * preferences for severe weather alerts and campus 
 * construction updates, and uses validation annotations
 * to ensure required information is provided.
 *
 * References:
 * Jakarta Bean Validation Documentation:
 * https://jakarta.ee/specifications/bean-validation/
 *
 * Spring Framework Validation Documentation:
 * https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html
 *
 * AI Assistance:
 * OpenAI ChatGPT was used to assist with 
 * configuring input validation annotations.
 */


package edu.usi.backend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Represents JSON data sent by the Angular frontend.
public record SubscriptionRequest(
    @NotBlank
    @Email
    String email,

    @NotNull
    Boolean weatherAlerts,

    @NotNull
    Boolean constructionAlerts
) {
}
