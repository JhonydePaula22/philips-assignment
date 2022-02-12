package com.waes.test.observer.impl;

import com.waes.test.model.ProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventTypeEnum;
import com.waes.test.observer.datasource.ObserversQueues;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class SupplyChainPropagationObserverTest {

    private final SupplyChainPropagationObserver supplyChainPropagationObserver = new SupplyChainPropagationObserver();

    @Test
    @SneakyThrows
    void should_notify_observer_and_see_the_message_on_the_queue() {
        //TODO REMOVE?
        Thread.sleep(5000);
        ProductDTO productDTO = new ProductDTO().id("1").name("name")
                .price(new BigDecimal("12.01")).quantity(1);

        supplyChainPropagationObserver.notifyObserver(productDTO, ActionEnum.CREATE, EventTypeEnum.PROPAGATE);

        Assertions.assertTrue(ObserversQueues.containsPropagateEventsToReprocess());
        Assertions.assertEquals(Event.builder()
                .id("1").name("name")
                .price(new BigDecimal("12.01"))
                .quantity(1)
                .action(ActionEnum.CREATE)
                .eventType(EventTypeEnum.PROPAGATE)
                .build(), ObserversQueues.poolPropagateEventsToBeReprocessed());
    }
}