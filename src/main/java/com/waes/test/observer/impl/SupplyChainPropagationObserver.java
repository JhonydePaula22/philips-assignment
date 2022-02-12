package com.waes.test.observer.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.Observer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link Component} class to handle Observer notifications whenever there is a need to propagate an Event to the upstream service.
 * Implements {@link Observer<ProductDTO>} interface.
 *
 * @author jonathanadepaula
 */
@Component("propagationObserver")
@Slf4j
public class SupplyChainPropagationObserver implements Observer<ProductDTO> {

    private final AmazonSQS amazonSQS;
    private final ObjectMapper objectMapper;
    private final String queue;

    public SupplyChainPropagationObserver(AmazonSQS amazonSQS, ObjectMapper objectMapper,
                                          @Value("${cloud.aws.queue.propagate.queue.full.url}") String queue) {
        this.amazonSQS = amazonSQS;
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    @Override
    public void notifyObserver(ProductDTO productDTO, ActionEnum action, EventTypeEnum eventType) {
        log.info("Adding new event to the queue to be reprocessed later. ProductDTO: {}, Action: {} ,EventType: {}", productDTO, action, eventType);
        Event event = Event.builder()
                .action(action)
                .eventType(eventType)
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .build();
        publishEvent(event);
    }

    public void publishEvent(Event message) {
        log.info("Publishing propagate event : {}", message);
        SendMessageRequest sendMessageRequest;
        try {
            sendMessageRequest = new SendMessageRequest().withQueueUrl(queue)
                    .withMessageBody(objectMapper.writeValueAsString(message))
                    .withMessageGroupId("Propagate Event Queue")
                    .withMessageDeduplicationId(UUID.randomUUID().toString());
            amazonSQS.sendMessage(sendMessageRequest);
            log.info("Event has been published in SQS.");
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException e : {} and stacktrace : {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception occurred while pushing event to sqs : {} and stacktrace ; {}", e.getMessage(), e);
        }
    }
}
