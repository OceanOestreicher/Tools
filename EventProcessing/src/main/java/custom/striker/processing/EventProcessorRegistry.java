package custom.striker.processing;

/**
 * Registry which allows an application to register and retrieve a {@link EventProcessor}
 */
public class EventProcessorRegistry {

    private static volatile EventProcessor processor = new DefaultEventProcessor();

    /**
     * Registers a processor for processing events in the application
     * @param eventProcessor The processor to register
     * @throws IllegalStateException Thrown if a new {@link EventProcessor} would be registered and an existing processor
     * is already running.
     */
    public static void registerProcessor(EventProcessor eventProcessor) throws IllegalStateException {
        if (processor.isRunning()) {
            throw new IllegalStateException("Cannot register a new EventProcessor when one is already running!");
        }
        processor = eventProcessor;
    }

    /**
     * Returns the processor stored in the registry
     * @return The registered processor
     */
    public static EventProcessor getProcessor() {
        return processor;
    }
}
