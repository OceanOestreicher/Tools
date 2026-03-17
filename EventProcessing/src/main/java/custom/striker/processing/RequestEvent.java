package custom.striker.processing;

/**
 * Event class for events related to requests. Requests have a 1 - 1 relationship with responses, so for every RequestEvent
 * there should be a corresponding ResponseEvent.
 */
public class RequestEvent extends Event {

    private final EventEnum<?> responseType;

    /**
     * Builds a RequestEvent with the given type and response type.
     * @param requestType The type of this event.
     * @param responseType The type of the corresponding response event.
     */
    public RequestEvent(EventEnum<?> requestType, EventEnum<?> responseType) {
        super(requestType);
        validateType(requestType);
        this.responseType = responseType;
    }

    /**
     * Builds a RequestEvent with the given type, response type, and payload.
     * @param requestType The type of this event.
     * @param responseType The type of the corresponding response event.
     * @param payload The payload of this event.
     */
    public RequestEvent(EventEnum<?> requestType, EventEnum<?> responseType, Object payload) {
        super(requestType, payload);
        validateType(requestType);
        this.responseType = responseType;
    }

    public EventEnum<?> getResponseType() {
        return responseType;
    }

    private void validateType(EventEnum<?> type) {
        if (type.getType()!= EventType.REQUEST) {
            throw new IllegalArgumentException("Invalid event type for NotificationEvent: " + type);
        }
    }
}
