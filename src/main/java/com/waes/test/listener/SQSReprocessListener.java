package com.waes.test.listener;

import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SQSReprocessListener {

    private final SupplyChainIntegration service;

    @SqsListener("${cloud.aws.queue.reprocess.queue.name}")
    public void receiveMessage(Event event) {
        log.info("Receiving message to reprocess error events");
        switch (event.getAction()) {
            case CREATE:
                log.info("Creating product from reprocessing error event {}", event);
                service.createNewProduct(new ProductDTO()
                        .id(event.getId())
                        .name(event.getName())
                        .price(event.getPrice())
                        .quantity(event.getQuantity())
                );
                break;
            case UPDATE:
                log.info("Updating product from reprocessing error event {}", event);
                service.updateProduct(new UpdateProductDTO()
                                .name(event.getName())
                                .price(event.getPrice())
                                .quantity(event.getQuantity())
                        , event.getId());
                break;
            case DELETE:
                log.info("Deleting product from reprocessing error event {}", event);
                service.deleteProduct(event.getId());
                break;
        }
    }
}
