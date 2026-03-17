package custom.striker.processing;

/**
 * Event which is used to represent a notification event in the application. These events do not generally
 * require a response.
 */
public class NotificationEvent extends Event {

    public NotificationEvent(EventEnum<?> type) {
        super(type);
        validateType(type);
    }

    public NotificationEvent(EventEnum<?> type, Object payload) {
        super(type, payload);
        validateType(type);
    }

    private void validateType(EventEnum<?> type) {
        if (type.getType()!= EventType.NOTIFICATION) {
            throw new IllegalArgumentException("Invalid event type for NotificationEvent: " + type);
        }
    }
}
