package com.waes.test.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.exception.BadRequestException;
import com.waes.test.exception.InternalServerErrorException;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.observer.Observer;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@Component
public class HttpUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client;
    private static ObjectMapper mapper;
    private static CircuitBreaker circuitBreaker;
    private static Observer<ProductDTO> productObserver;

    public HttpUtils(OkHttpClient client,
                     ObjectMapper mapper,
                     CircuitBreaker circuitBreaker,
                     @Qualifier("errorObserver")
                     Observer<ProductDTO> observer) {
        HttpUtils.client = client;
        HttpUtils.mapper = mapper;
        HttpUtils.circuitBreaker = circuitBreaker;
        HttpUtils.productObserver = observer;
    }

    public <RESPONSE> RESPONSE executeCall(Supplier<RESPONSE> supplier) {
        Supplier<RESPONSE> decorated = circuitBreaker.decorateSupplier(supplier);
        return decorated.get();
    }

    public <RESPONSE> RESPONSE executeCallWithObserver(Supplier<RESPONSE> supplier, ProductDTO productDTO, EventEnum eventType) {
        try {
            return executeCall(supplier);
        } catch (InternalServerErrorException | CallNotPermittedException e) {
            productObserver.notifyObserver(productDTO, eventType);
            throw new InternalServerErrorException(e.getMessage(), productDTO.getId(), eventType);
        }
    }

    public <RESPONSE> RESPONSE executeGetRequest(String url, Class<RESPONSE> clazz) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return executeRequest(request, clazz);
    }

    public <RESPONSE> RESPONSE executeDeleteRequest(String url, Class<RESPONSE> clazz) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        return executeRequest(request, clazz);
    }

    public <BODY, RESPONSE> RESPONSE executePostRequest(String url, BODY body, Class<RESPONSE> clazz) {
        try {
            RequestBody requestBody = RequestBody.create(mapper.writeValueAsString(body), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            return executeRequest(request, clazz);
        } catch (IOException e) {
            log.error("Failed to serialize the request body into {} object.", clazz.getName(), e);
            throw new InternalServerErrorException(String.format("Failed to serialize the request body into %s object.", clazz.getName()));
        }
    }

    private <RESPONSE> RESPONSE executeRequest(Request request, Class<RESPONSE> clazz) {
        log.info("Executing request to url {}.", request.url());
        Response response = null;
        try {
            response = client.newCall(request).execute();
            validateStatusCode(response);
            return validateResponseBody(response.body(), clazz);
        } catch (IOException e) {
            log.error("Failed to execute request to url {}.", request.url(), e);
            throw new InternalServerErrorException("Failed to access resource from 3rd party API.");
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }

    private <RESPONSE> RESPONSE validateResponseBody(ResponseBody responseBody, Class<RESPONSE> clazz) {
        if (Objects.nonNull(responseBody) && responseBody.contentLength() > 0) {
            try {
                return mapper.readValue(responseBody.string(), clazz);
            } catch (IOException e) {
                log.error("Failed to deserialize the response body into {} object.", clazz.getName(), e);
                throw new InternalServerErrorException(String.format("Failed to deserialize the response body into %s object.", clazz.getName()));
            }
        }
        return null;
    }

    private void validateStatusCode(Response response) {
        if (!response.isSuccessful()) {
            if (response.code() == 404) {
                throw new BadRequestException("Failed to access the resources on the 3rd party API due to a Bad Request. Please check the parameters and make the request again.");
            }
            throw new InternalServerErrorException("Something went wrong! Failed to access the resources on the 3rd party API.");
        }
    }

}
