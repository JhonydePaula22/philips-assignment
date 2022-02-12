package com.waes.test.scheduler;

import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.Event;
import com.waes.test.observer.datasource.ObserversQueues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * {@link Component} to run every given time to process retry {@link com.waes.test.model.event.Event}.
 *
 * @author jonathanadepaula
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReprocessScheduler {

    private final SupplyChainIntegration service;

    @Scheduled(cron = "*/5 * * * * *")
    public void run() {
        while (ObserversQueues.containsRetryEventsToReprocess()) {
            log.info("Running scheduler to reprocess error events");
            Event event = ObserversQueues.poolEventsRetryToBeReprocessed();
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
}
