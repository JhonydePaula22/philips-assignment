package com.waes.test.observer.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventTypeEnum;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class ErrorsObserverTest {

    @Mock
    private ObjectMapper mapper;
    @Mock
    private AmazonSQS sqs;

    private ErrorsObserver observer;

    @BeforeEach
    void setup() {
        observer = new ErrorsObserver(sqs, mapper, "url");
    }

    @Test
    @SneakyThrows
    void should_send_message_to_the_queue() {
        ProductDTO productDTO = new ProductDTO().id("1").name("name")
                .price(new BigDecimal("12.01")).quantity(1);
        Event event = Event.builder()
                .id("1").name("name")
                .price(new BigDecimal("12.01"))
                .quantity(1)
                .action(ActionEnum.CREATE)
                .eventType(EventTypeEnum.RETRY)
                .build();

        Mockito.when(mapper.writeValueAsString(event)).thenReturn("{}");
        Mockito.when(sqs.sendMessage(ArgumentMatchers.any(SendMessageRequest.class))).thenReturn(null);

        observer.notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.RETRY);

        Mockito.verify(mapper, Mockito.times(1)).writeValueAsString(event);
        Mockito.verify(sqs, Mockito.times(1)).sendMessage(ArgumentMatchers.any(SendMessageRequest.class));
    }

    @Test
    @SneakyThrows
    void should_send_fail_to_message_to_the_queue_due_to_serialization_error() {
        ProductDTO productDTO = new ProductDTO().id("1").name("name")
                .price(new BigDecimal("12.01")).quantity(1);
        Event event = Event.builder()
                .id("1").name("name")
                .price(new BigDecimal("12.01"))
                .quantity(1)
                .action(ActionEnum.CREATE)
                .eventType(EventTypeEnum.RETRY)
                .build();

        Answer<String> answer = invocation -> {
            throw new IOException("failed");
        };

        Mockito.when(mapper.writeValueAsString(event)).thenAnswer(answer);

        observer.notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.RETRY);

        Mockito.verify(mapper, Mockito.times(1)).writeValueAsString(event);
        Mockito.verify(sqs, Mockito.times(0)).sendMessage(ArgumentMatchers.any());
    }

    @Test
    @SneakyThrows
    void should_send_fail_to_message_to_the_queue_due_to_unknown_error() {
        ProductDTO productDTO = new ProductDTO().id("1").name("name")
                .price(new BigDecimal("12.01")).quantity(1);
        Event event = Event.builder()
                .id("1").name("name")
                .price(new BigDecimal("12.01"))
                .quantity(1)
                .action(ActionEnum.CREATE)
                .eventType(EventTypeEnum.RETRY)
                .build();

        Answer<String> answer = invocation -> {
            throw new Exception("failed");
        };

        Mockito.when(mapper.writeValueAsString(event)).thenAnswer(answer);

        observer.notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.RETRY);

        Mockito.verify(mapper, Mockito.times(1)).writeValueAsString(event);
        Mockito.verify(sqs, Mockito.times(0)).sendMessage(ArgumentMatchers.any());
    }
}