package com.waes.test.observer.datasource;

import com.waes.test.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Component
public class ObserversQueues {

    private static final Queue<Event> retryQueue = new LinkedList<>();
    private static final Queue<Event> propagateQueue = new LinkedList<>();

    public static boolean containsPropagateEventsToReprocess() {
        return !propagateQueue.isEmpty();
    }

    public static Event poolPropagateEventsToBeReprocessed() {
        log.info("Retrieving event from the queue to be reprocessed.");
        return propagateQueue.poll();
    }

    public static void offerPropagateEventToBeReprocessed(Event event) {
        log.info("Adding event to the queue to be reprocessed.");
        propagateQueue.offer(event);
    }

    public static boolean containsRetryEventsToReprocess() {
        return !retryQueue.isEmpty();
    }

    public static Event poolEventsRetryToBeReprocessed() {
        log.info("Retrieving event from the queue to be reprocessed.");
        return retryQueue.poll();
    }

    public static void offerRetryEventToBeReprocessed(Event event) {
        log.info("Adding event to the queue to be reprocessed.");
        retryQueue.offer(event);
    }
}
