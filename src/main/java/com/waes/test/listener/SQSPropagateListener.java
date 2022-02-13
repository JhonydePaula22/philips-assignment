package com.waes.test.listener;

import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * {@link Component} class to handle communication between the application and SQS.
 * Specifically this bean handles the propagation events.
 *
 * @author jonathanadepaula
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sqs.enabled", havingValue = "true")
public class SQSPropagateListener {

    private final SupplyChainIntegration service;

    /**
     * Consumes {@link Event} and process them accordingly.
     * @param event
     */
    @SqsListener("${cloud.aws.queue.propagate.queue.name}")
    public void receiveMessage(Event event) {
        log.info("Receiving message to propagate events");
        switch (event.getAction()) {
            case CREATE:
                log.info("Creating product from propagation event {}", event);
                service.createNewProduct(new ProductDTO()
                        .id(event.getId())
                        .name(event.getName())
                        .price(event.getPrice())
                        .quantity(event.getQuantity())
                );
                break;
            case UPDATE:
                log.info("Updating product from propagation event {}", event);
                service.updateProduct(new UpdateProductDTO()
                                .name(event.getName())
                                .price(event.getPrice())
                                .quantity(event.getQuantity())
                        , event.getId());
                break;
            case DELETE:
                log.info("Deleting product from propagation event {}", event);
                service.deleteProduct(event.getId());
                break;
        }
    }
}
