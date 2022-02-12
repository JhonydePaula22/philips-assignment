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
 * {@link Component} class to handle Observer notifications whenever there is a need to reprocess an Event.
 * Implements {@link Observer<ProductDTO>} interface.
 *
 * @author jonathanadepaula
 */
@Component("errorObserver")
@Slf4j
public class ErrorsObserver implements Observer<ProductDTO> {

    private final AmazonSQS amazonSQS;
    private final ObjectMapper objectMapper;
    private final String queue;

    public ErrorsObserver(AmazonSQS amazonSQS, ObjectMapper objectMapper,
                          @Value("${cloud.aws.queue.reprocess.queue.full.url}") String queue) {
        this.amazonSQS = amazonSQS;
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    @Override
    public void notifyObserver(ProductDTO productDTO, ActionEnum action, EventTypeEnum eventType) {
        log.info("Adding new event to the queue to be reprocessed later. ProductDTO: {}, Action: {}, EventType: {}", productDTO, action, eventType);
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
        log.info("Generating event : {}", message);
        SendMessageRequest sendMessageRequest = null;
        try {
            sendMessageRequest = new SendMessageRequest().withQueueUrl(queue)
                    .withMessageBody(objectMapper.writeValueAsString(message))
                    .withMessageGroupId("Reprocess Event Queue")
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
