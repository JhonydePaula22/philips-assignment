package com.waes.test.config;

import com.waes.test.exception.BadRequestException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
public class ResilienceConfiguration {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ResilienceConfiguration(@Value("${cb.sliding.window.size}") int slidingWindowSize,
                                   @Value("${cb.failure.rate.threshold}") float failureRateThreshold,
                                   @Value("${cb.wait.duration.in.open.state}") long waitDurationInOpenState,
                                   @Value("${cb.permitted.number.of.calls.in.half.open.state}") int permittedNumberOfCallsInHalfOpenState,
                                   @Value("${cb.name}") String cbName,
                                   @Value("${retry.maxAttempts}") int retryMaxAttempts,
                                   @Value("${retry.waitDuration}") long retryWaitDuration,
                                   @Value("${retry.name}") String retryName) {

        circuitBreaker = instantiateCircuitBreaker(slidingWindowSize, failureRateThreshold, waitDurationInOpenState, permittedNumberOfCallsInHalfOpenState, cbName);
        retry = instantiateRetry(retryMaxAttempts, retryWaitDuration, retryName);
    }

    private CircuitBreaker instantiateCircuitBreaker(int slidingWindowSize, float failureRateThreshold, long waitDurationInOpenState, int permittedNumberOfCallsInHalfOpenState, String cbName) {
        final CircuitBreaker circuitBreaker;
        // If 70% of the requests fail the circuit will get opened. After 10 seconds it will let 4 requests pass to check the healthiness of the 3rd party API.
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(slidingWindowSize)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .build();

        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(cbConfig);
        circuitBreaker = cbRegistry.circuitBreaker(cbName);
        return circuitBreaker;
    }

    private Retry instantiateRetry(int retryMaxAttempts, long retryWaitDuration, String retryName) {
        final Retry retry;
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(retryMaxAttempts)
                .waitDuration(Duration.of(retryWaitDuration, SECONDS))
                .ignoreExceptions(BadRequestException.class)
                .build();

        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        retry = retryRegistry.retry(retryName);
        return retry;
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        return circuitBreaker;
    }

    @Bean
    public Retry retry() {
        return retry;
    }
}
