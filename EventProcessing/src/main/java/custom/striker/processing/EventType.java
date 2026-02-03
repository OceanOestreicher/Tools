package custom.striker.processing;

/**
 * Interface for enum that is used as the type for an {@link Event}
 * @param <T> The type of the enum that implements this interface
 */
public interface EventType<T extends Enum<T>> {

    default T getType() {
        return (T) this;
    }
}
