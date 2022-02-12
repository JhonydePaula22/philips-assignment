package com.waes.test.observer.datasource;

import com.waes.test.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link Component} that contains the Queues that holds the events that should be reprocessed or propagated to upstream service.
 *
 * @author jonathanadepaula
 */
@Slf4j
@Component
public class ObserversQueues {

    private static final Queue<Event> retryQueue = new LinkedList<>();
    private static final Queue<Event> propagateQueue = new LinkedList<>();

    /**
     * Check whether there is any {@link Event} to be propagated.
     *
     * @return boolean
     */
    public static boolean containsPropagateEventsToReprocess() {
        return !propagateQueue.isEmpty();
    }

    /**
     * Gets an {@link Event} to the propagate queue.
     *
     * @return {@link Event}
     */
    public static Event poolPropagateEventsToBeReprocessed() {
        log.info("Retrieving event from the queue to be reprocessed.");
        return propagateQueue.poll();
    }

    /**
     * Adds an {@link Event} to the propagate queue.
     */
    public static void offerPropagateEventToBeReprocessed(Event event) {
        log.info("Adding event to the queue to be reprocessed.");
        propagateQueue.offer(event);
    }

    /**
     * Check whether there is any {@link Event} to be retried.
     *
     * @return boolean
     */
    public static boolean containsRetryEventsToReprocess() {
        return !retryQueue.isEmpty();
    }

    /**
     * Gets an {@link Event} to the retry queue.
     *
     * @return {@link Event}
     */
    public static Event poolEventsRetryToBeReprocessed() {
        log.info("Retrieving event from the queue to be reprocessed.");
        return retryQueue.poll();
    }

    /**
     * Adds an {@link Event} to the retry queue.
     */
    public static void offerRetryEventToBeReprocessed(Event event) {
        log.info("Adding event to the queue to be reprocessed.");
        retryQueue.offer(event);
    }
}
