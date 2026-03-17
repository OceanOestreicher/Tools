package custom.striker.processing;

/**
 * Event class for events related to responses. Responses have a  1 - 1 relationship with requests, so every response
 * event should have a corresponding request event.
 */
public class ResponseEvent extends Event {

    /**
     * Creates a new ResponseEvent with the provided type. The type must be of EventType.RESPONSE
     * @param responseType The type of the response event, which must be of EventType.RESPONSE
     */
    public ResponseEvent(EventEnum<?> responseType) {
        super(responseType);
        validateType(responseType);
    }

    /**
     * Creates a new ResponseEvent with the provided type and payload. The type must be of EventType.RESPONSE
     * @param responseType The type of the response event, which must be of EventType.RESPONSE
     * @param payload The payload of the response event, which can be any object relevant to the response
     */
    public ResponseEvent(EventEnum<?> responseType, Object payload) {
        super(responseType, payload);
        validateType(responseType);
    }

    private void validateType(EventEnum<?> responseType) {
        if (responseType.getType() != EventType.RESPONSE) {
            throw new IllegalArgumentException("Invalid event type for ResponseEvent: " + responseType);
        }
    }
}
