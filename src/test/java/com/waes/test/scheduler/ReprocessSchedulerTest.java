package com.waes.test.scheduler;

import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.EventEnum;
import com.waes.test.observer.impl.ErrorsObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class ReprocessSchedulerTest {

    @Mock
    private SupplyChainIntegration service;

    @InjectMocks
    private ReprocessScheduler scheduler;

    private final ErrorsObserver errorsObserver = new ErrorsObserver();

    @Test
    void should_do_nothing_when_there_is_no_event_to_be_processed() {
        scheduler.run();
        Mockito.verify(service, Mockito.times(0)).createNewProduct(ArgumentMatchers.any(ProductDTO.class));
        Mockito.verify(service, Mockito.times(0)).updateProduct(ArgumentMatchers.any(UpdateProductDTO.class), ArgumentMatchers.anyString());
        Mockito.verify(service, Mockito.times(0)).deleteProduct(ArgumentMatchers.anyString());
    }

    @Test
    void should_process_events_when_there_is_are_events_to_be_processed() {
        ProductDTO productDTO = new ProductDTO().id("1").name("name")
                .price(new BigDecimal("12.01")).quantity(1);

        errorsObserver.notifyObserver(productDTO, EventEnum.CREATE);
        errorsObserver.notifyObserver(productDTO, EventEnum.UPDATE);
        errorsObserver.notifyObserver(productDTO, EventEnum.DELETE);

        scheduler.run();

        Mockito.verify(service, Mockito.times(1)).createNewProduct(ArgumentMatchers.any(ProductDTO.class));
        Mockito.verify(service, Mockito.times(1)).updateProduct(ArgumentMatchers.any(UpdateProductDTO.class), ArgumentMatchers.anyString());
        Mockito.verify(service, Mockito.times(1)).deleteProduct(ArgumentMatchers.anyString());
    }

}