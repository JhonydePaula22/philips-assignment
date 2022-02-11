package com.waes.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.exception.BadRequestException;
import com.waes.test.exception.InternalServerErrorException;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.observer.Observer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.SneakyThrows;
import okhttp3.MediaType;
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
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class HttpUtilsTest {

    private OkHttpClient client = new OkHttpClient();
    @Mock
    private ObjectMapper mapper;

    CircuitBreakerConfig config = CircuitBreakerConfig.ofDefaults();
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
    private CircuitBreaker circuitBreaker = registry.circuitBreaker("cb-name");

    @Mock
    private Observer<ProductDTO> productObserver;

    private HttpUtils httpUtils;

    private MockWebServer mockWebServer;

    private static final String CUSTOMER_PATH = "/resource";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String url;


    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(); //initialise mock web server

        URL mockServerBaseUrl = mockWebServer.url("").url(); //get base url of mockwebserver
        url = new URL(mockServerBaseUrl, CUSTOMER_PATH).toString(); //append specific url for making request

        httpUtils = new HttpUtils(client, mapper, circuitBreaker, productObserver);
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

        Mockito.doNothing().when(productObserver).notifyObserver(new ProductDTO().id("1"), EventEnum.DELETE);
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCallWithObserver(deleteProductSupplier, new ProductDTO().id("1"), EventEnum.DELETE));
    }

    @Test
    @SneakyThrows
    void should_execute_delete_request_with_observer_successfuly() {
        Supplier<Void> deleteProductSupplier = () -> httpUtils.executeDeleteRequest(this.url, Void.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Void actual = httpUtils.executeCallWithObserver(deleteProductSupplier, new ProductDTO().id("1"), EventEnum.DELETE);

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
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductDTO.class);

        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(getProductsDTOSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_get_request_and_fail_to_deserialize_response() {
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest(this.url, ProductDTO.class);

        Answer<String> answer = invocation -> {
            throw new IOException("failed");
        };

        Mockito.when(mapper.readValue("{}", ProductDTO.class)).thenAnswer(answer);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(getProductsDTOSupplier));
    }

    @Test
    @SneakyThrows
    void should_execute_get_request_and_fail_due_to_wrong_url() {
        Supplier<ProductDTO> getProductsDTOSupplier = () -> httpUtils.executeGetRequest("http://localhost:1234", ProductDTO.class);

        Assertions.assertThrows(InternalServerErrorException.class, () -> httpUtils.executeCall(getProductsDTOSupplier));
    }

}