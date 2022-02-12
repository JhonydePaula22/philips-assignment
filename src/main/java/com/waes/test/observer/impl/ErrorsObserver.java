package com.waes.test.observer.impl;

import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.Observer;
import com.waes.test.observer.datasource.ObserversQueues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link Component} class to handle Observer notifications whenever there is a need to reprocess an Event.
 * Implements {@link Observer<ProductDTO>} interface.
 *
 * @author jonathanadepaula
 */
@Component("errorObserver")
@Slf4j
public class ErrorsObserver implements Observer<ProductDTO> {

    @Override
    public void notifyObserver(ProductDTO productDTO, ActionEnum action, EventTypeEnum eventType) {
        log.info("Adding new event to the queue to be reprocessed later. ProductDTO: {}, Action: {}, EventType: {}", productDTO, action, eventType);
        ObserversQueues.offerRetryEventToBeReprocessed(Event.builder()
                .action(action)
                .eventType(eventType)
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .build());
    }
}
