package custom.striker.processing;

import java.util.List;

/**
 * Interface for a class which consumes events from a registered {@link EventProcessor}.
 */
public interface EventConsumer {

    /**
     * Subscribes this consumer to receive events corresponding to type. By default, this method will register the consumer
     * to the configured processor in the {@link EventProcessorRegistry}
     * @param type The type of events the consumer would like to receive
     */
   default void subscribe(EventEnum<?> type) {
        EventProcessorRegistry.getProcessor().registerConsumer(type, this);
   }

    /**
     * Subscribes this consumer to receive events for all the corresponding types. By default, this method
     * will register the consumer to the configured processor in the {@link EventProcessorRegistry}
     * @param types The types of events the consumer would like to receive
     */
   default void subscribe(List<EventEnum<?>> types) {
       types.forEach(this::subscribe);
   }

    /**
     * Unsubscribes this consumer from receiving events corresponding to type. By default, this method will unregister the consumer
     * from the configured processor in the {@link EventProcessorRegistry}
     * @param type The type of events the consumer would like to stop receiving
     */
   default void unsubscribe(EventEnum<?> type) {
       EventProcessorRegistry.getProcessor().unregisterConsumer(type, this);
   }

    /**
     * Unsubscribes this consumer from receiving events for all types. By default, this method will unregister the consumer
     * from the configured processor in the {@link EventProcessorRegistry}
     */
   default void unsubscribe() {
         EventProcessorRegistry.getProcessor().unregisterConsumer(this);
   }

    /**
     * Consumes the event
     * @param event The event to consume
     */
   void consume(Event event);
}
