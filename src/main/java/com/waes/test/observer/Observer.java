package com.waes.test.observer;

import com.waes.test.model.event.ActionEnum;
import com.waes.test.model.event.EventTypeEnum;

/**
 * Interface to define the contract of all operations that need to be Observed.
 *
 * @author jonathanadepaula
 */
public interface Observer<T> {

    /**
     * Notifies the observer that an {@link com.waes.test.model.event.Event} must be created and processed later.
     *
     * @param ob
     * @param actionEnum {@link ActionEnum}
     * @param eventType  {@link EventTypeEnum}
     */
    void notifyObserver(T ob, ActionEnum actionEnum, EventTypeEnum eventType);
}
