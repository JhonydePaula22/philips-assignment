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

@Component
@Slf4j
@RequiredArgsConstructor
public class PropagateScheduler {

    private final SupplyChainIntegration service;

    // Job will run every minute.
    // Depending on the business criteria it can be increased or diminished.
    @Scheduled(cron = "*/1 * * * * *")
    public void run() {
        while (ObserversQueues.containsPropagateEventsToReprocess()) {
            log.info("Running scheduler to propagate events");
            Event event = ObserversQueues.poolPropagateEventsToBeReprocessed();
            switch (event.getEventType()) {
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
}
