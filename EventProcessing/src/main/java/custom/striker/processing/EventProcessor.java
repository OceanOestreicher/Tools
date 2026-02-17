package custom.striker.processing;

/**
 * Interface for a class that handles the generation and consumption of events for the application
 */
public interface EventProcessor {

    /**
     * Starts the processor and enables it to begin receiving and sending events from {@link EventProducer}s and
     * {@link EventConsumer}s
     * @throws IllegalStateException if this method has been already been called
     */
    void startup() throws IllegalStateException;

    /**
     * Shuts down the processor
     */
    void shutdown();

    /**
     * Whether the processor has been started or not
     * @return true if the processor has started
     */
    boolean isRunning();

    /**
     * Registers a consumer to receive events for the provided event type
     * @param type Type of events the subscriber will receive
     * @param consumer The consumer to subscribe
     */
    void registerConsumer(EventType<?> type, EventConsumer consumer);

    /**
     * Unregisters a consumer from receiving any events
     * @param consumer The consumer to unregister
     */
    void unregisterConsumer(EventConsumer consumer);

    /**
     * Unregisters a consumer from receiving events for the provided event type
     * @param type Type of events the subscriber will stop receiving
     * @param consumer The consumer to unregister
     */
    void unregisterConsumer(EventType<?> type, EventConsumer consumer);

    /**
     * Enqueues an event to be processed and consumed
     * @param event The event to enqueue
     */
    void enqueue(Event event);
}
