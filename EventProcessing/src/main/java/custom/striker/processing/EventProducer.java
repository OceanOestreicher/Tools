package custom.striker.processing;

/**
 * Interface for a class that produces {@link Event}s. These are processed in an {@link EventProcessor} and consumed
 * by 1 or more {@link EventConsumer}s
 */
public interface EventProducer {

    /**
     * Produces the event and enqueues it for processing. By default, will enqueue the event from produceEvent using the
     * configured processor in {@link EventProcessorRegistry}
     * @param event The event that the producer produced
     */
    default void produce(Event event) {
        EventProcessorRegistry.getProcessor().enqueue(event);
    }
}
