package custom.striker.processing;

import custom.striker.processing.payload.EmptyPayload;

/**
 * Base class which represents an event that is produced by a {@link EventProducer}, processed by a
 * {@link EventProcessor} and consumed by 1 or more {@link EventConsumer}s
 */
public abstract class Event {

    private final EventType<? extends Enum<?>> type;
    private final Object payload;

    public Event(EventType<?> type) {
        this.type = type;
        payload = new EmptyPayload();
    }

    public Event(EventType<?> type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public <T extends Enum<T>> T getType() {
        return (T) type.getType();
    }

    public <T> T getPayload() {
        return (T) payload;
    }
}
