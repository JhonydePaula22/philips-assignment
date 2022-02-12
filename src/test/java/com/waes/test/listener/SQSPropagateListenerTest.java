package com.waes.test.listener;

import com.waes.test.integration.SupplyChainIntegration;
import com.waes.test.model.ProductDTO;
import com.waes.test.model.UpdateProductDTO;
import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.Event;
import com.waes.test.model.event.EventTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class SQSPropagateListenerTest {

    @Mock
    private SupplyChainIntegration service;

    @InjectMocks
    private SQSPropagateListener listener;

    @Test
    void should_process_events_when_there_is_are_events_to_be_processed() {
        listener.receiveMessage(Event.builder().id("1").name("name").quantity(1).price(new BigDecimal("12.01")).eventType(EventTypeEnum.PROPAGATE).action(ActionEnum.CREATE).build());
        listener.receiveMessage(Event.builder().id("1").name("name").quantity(1).price(new BigDecimal("12.01")).eventType(EventTypeEnum.PROPAGATE).action(ActionEnum.UPDATE).build());
        listener.receiveMessage(Event.builder().id("1").name("name").quantity(1).price(new BigDecimal("12.01")).eventType(EventTypeEnum.PROPAGATE).action(ActionEnum.DELETE).build());

        Mockito.verify(service, Mockito.times(1)).createNewProduct(ArgumentMatchers.any(ProductDTO.class));
        Mockito.verify(service, Mockito.times(1)).updateProduct(ArgumentMatchers.any(UpdateProductDTO.class), ArgumentMatchers.anyString());
        Mockito.verify(service, Mockito.times(1)).deleteProduct(ArgumentMatchers.anyString());
    }

}