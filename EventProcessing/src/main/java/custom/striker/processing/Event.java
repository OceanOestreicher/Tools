package custom.striker.processing;

import custom.striker.processing.payload.EmptyPayload;

/**
 * Base class which represents an event that is produced by a {@link EventProducer}, processed by a
 * {@link EventProcessor} and consumed by 1 or more {@link EventConsumer}s
 */
public abstract class Event {

    private final EventEnum<? extends Enum<?>> type;
    private final Object payload;

    public Event(EventEnum<?> type) {
        this.type = type;
        payload = new EmptyPayload();
    }

    public Event(EventEnum<?> type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public <T extends EventEnum<?>> T getType() {
        return (T) type.getEnum();
    }

    public <T> T getPayload() {
        if (payload == null) {
            return null;
        }
        return (T) payload;
    }
}
