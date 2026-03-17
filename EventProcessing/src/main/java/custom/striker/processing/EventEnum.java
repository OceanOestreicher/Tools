package custom.striker.processing;

/**
 * Interface for enum that is used as the type for an {@link Event}
 * @param <T> The type of the enum that implements this interface
 */
public interface EventEnum<T extends Enum<T>> {

    @SuppressWarnings("unchecked")
    default T getEnum() {
        return (T) this;
    }

    EventType getType();
}
