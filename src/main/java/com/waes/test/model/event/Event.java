package com.waes.test.model.event;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * Event class which represents the events that should be reprocessed or propagated to upstream service.
 *
 * @author jonathanadepaula
 */
@Data
@Builder
public class Event {

    @NonNull
    private ActionEnum action;
    @NonNull
    private EventTypeEnum eventType;
    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
