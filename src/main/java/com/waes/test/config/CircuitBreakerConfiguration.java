package com.waes.test.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerConfiguration(@Value("${cb.sliding.window.size}") int slidingWindowSize,
                                       @Value("${cb.failure.rate.threshold}") float failureRateThreshold,
                                       @Value("${cb.wait.duration.in.open.state}") long waitDurationInOpenState,
                                       @Value("${cb.permitted.number.of.calls.in.half.open.state}") int permittedNumberOfCallsInHalfOpenState,
                                       @Value("${cb.name}") String cbName) {

        // If 70% of the requests fail the circuit will get opened. After 10 seconds it will let 4 requests pass to check the healthiness of the 3rd party API.
        CircuitBreakerConfig config = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(slidingWindowSize)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker(cbName);
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        return circuitBreaker;
    }
}
