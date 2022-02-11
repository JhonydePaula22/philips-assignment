package com.waes.test.observer;

import com.waes.test.model.event.EventEnum;

public interface Observer<T> {

    void notifyObserver(T ob, EventEnum eventType);
}
