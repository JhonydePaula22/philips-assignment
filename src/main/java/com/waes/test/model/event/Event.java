package com.waes.test.model.event;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class Event {

    @NonNull
    private EventEnum eventType;

    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
