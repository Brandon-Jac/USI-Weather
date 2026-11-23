
/*
 * SubscriberRepository.java
 *
 * Provides database access for email subscriptions
 * using Spring Data JPA. Supports saving and retrieving
 * subscriber records and checking whether an email
 * address is already registered.
 *
 * References:
 * Spring Data JPA Documentation:
 * https://docs.spring.io/spring-data/jpa/reference/
 *
 * Spring Data Repository Documentation:
 * https://docs.spring.io/spring-data/jpa/reference/repositories/
 */


package edu.usi.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    // Checks whether an email is already registered
    boolean existsByEmailIgnoreCase(String email);
}
