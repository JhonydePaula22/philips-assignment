package com.waes.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.exception.BadRequestException;
import com.waes.test.exception.InternalServerErrorException;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.Observer;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.collection.Stream;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.SECONDS;

@ExtendWith(MockitoExtension.class)
class HttpUtilsTest {

    private OkHttpClient client;

    @Mock
    private ObjectMapper mapper;

    private CircuitBreaker circuitBreaker;

    private Retry retry;

    @Mock
    private Observer<ProductDTO> productObserver;

    private HttpUtils httpUtils;

    private MockWebServer mockWebServer;

    private static final String CUSTOMER_PATH = "/resource";

    private String url;


    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        URL mockServerBaseUrl = mockWebServer.url("").url();

        CircuitBreakerConfig config = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(70.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(4)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("cb-name");

        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.of(0, SECONDS))
                .ignoreExceptions(BadRequestException.class)
                .build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        retry = retryRegistry.retry("retry-name");

        client = new OkHttpClient();

        url = new URL(mockServerBaseUrl, CUSTOMER_PATH).toString();

        httpUtils = new HttpUtils(client, mapper, circuitBreaker, retry, productObserver);
    }

    @Test
    @SneakyThrows
    void should_execute_get_request_successfully() {
        ProductDTO expected = new ProductDTO();
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductDTO.class);

        Mockito.when(mapper.readValue("{}", ProductDTO.class)).thenReturn(expected);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        ProductDTO actual = httpUtils.executeCall(getProductsDTOSupplier);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void should_execute_post_request_successfully() {
        ProductDTO expected = new ProductDTO();
        Supplier<ProductDTO> createNewProductSupplier = () -> httpUtils.executePostRequest(this.url, expected, ProductDTO.class);

        Mockito.when(mapper.writeValueAsString(expected)).thenReturn("{}");
        Mockito.when(mapper.readValue("{}", ProductDTO.class)).thenReturn(expected);
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{}"));

        ProductDTO actual = httpUtils.executeCall(createNewProductSupplier);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void should_execute_post_request_and_fail_to_serialize_object_to_json() {
        ProductDTO expected = new ProductDTO();
        Supplier<ProductDTO> createNewProductSupplier = () -> httpUtils.executePostRequest(this.url, expected, ProductDTO.class);

        Answer<String> answer = invocation -> {
            throw new IOException("failed");
        };

        Mockito.when(mapper.writeValueAsString(expected)).thenAnswer(answer);

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(createNewProductSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_delete_request_with_observer_and_notify_when_status_500() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(this.url, Void.class);

        Mockito.doNothing().when(productObserver).notifyObserver(new ProductDTO().id("1"), ActionEnum.DELETE, EventTypeEnum.RETRY);
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCallWithObserver(deleteProductSupplier, new ProductDTO().id("1"), ActionEnum.DELETE));
    }

    @Test
    @SneakyThrows
    void should_execute_delete_request_with_observer_successfuly() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(this.url, Void.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Void actual = httpUtils.executeCallWithObserver(deleteProductSupplier, new ProductDTO().id("1"), ActionEnum.DELETE);

        Assertions.assertNull(actual);
    }

    @Test
    @SneakyThrows
    void should_execute_request_and_return_404_error() {
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductDTO.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));

        Assertions.assertThrows(BadRequestException.class, () -> httpUtils.executeCall(getProductsDTOSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_request_and_return_500_error() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(this.url, Void.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(deleteProductSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_post_request_and_fail_to_deserialize_response() {
        ProductDTO expected = new ProductDTO();
        Supplier<ProductDTO> createNewProductSupplier = () -> httpUtils.executePostRequest(this.url, expected, ProductDTO.class);

        Answer<String> answer = invocation -> {
            throw new IOException("failed");
        };

        Mockito.when(mapper.writeValueAsString(expected)).thenReturn("{}");
        Mockito.when(mapper.readValue("{}", ProductDTO.class)).thenAnswer(answer);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(createNewProductSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_delete_request_and_fail_due_to_wrong_url() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest("http://localhost:1234", Void.class);

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(deleteProductSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_delete_request_and_fail_and_open_cb() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(this.url, Void.class);

        Stream.range(0, 12).forEach((num) -> mockWebServer.enqueue(new MockResponse().setResponseCode(500)));

        for (int i = 0; i <= 11; i++) {
            if (i < 10) {
                Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(deleteProductSupplier));
            } else {
                Assertions.assertThrows(CallNotPermittedException.class, () -> httpUtils.executeCall(deleteProductSupplier));
            }
        }
    }

    @Test
    @SneakyThrows
    void should_execute_request_and_return_500_error_and_retry_3_times() {
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductDTO.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(getProductsDTOSupplier));

        Assertions.assertEquals(3, mockWebServer.getRequestCount());
    }

}