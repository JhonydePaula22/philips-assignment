package com.waes.test.observer.impl;

import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventEnum;
import com.waes.test.observer.Observer;
import com.waes.test.observer.datasource.ObserversQueues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("propagationObserver")
@Slf4j
public class SupplyChainPropagationObserver implements Observer<ProductDTO> {

    @Override
    public void notifyObserver(ProductDTO productDTO, EventEnum eventType) {
        log.info("Adding new event to the queue to be reprocessed later. ProductDTO: {}, EventType: {}", productDTO, eventType);
        ObserversQueues.offerPropagateEventToBeReprocessed(Event.builder()
                .eventType(eventType)
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .build());
    }
}
