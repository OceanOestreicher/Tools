package custom.striker.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Default implementation of {@link EventProcessor}.
 */
public class DefaultEventProcessor implements EventProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventProcessor.class);
    private static final ConcurrentMap<EventEnum<? extends Enum<?>>, List<EventConsumer>> typeToConsumerMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<EventConsumer, List<EventEnum<? extends Enum<?>>>> consumerToTypeMap = new ConcurrentHashMap<>();
    private static final Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();
    private static final Object lock = new Object();
    private static volatile boolean process = true;
    private ProcessorThread processorThread = null;

    /** {@inheritDoc} */
    public void startup() throws IllegalStateException {
        if (processorThread != null) {
            throw new IllegalStateException("Default Event Processor is already running!");
        }

        log.debug("Default Event Processor Starting");
        processorThread = new ProcessorThread();
        processorThread.start();
    }

    /** {@inheritDoc} */
    public void shutdown() {
        log.debug("Default Event Processor Shutting Down");
        process = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunning() {
        return processorThread != null;
    }

    /** {@inheritDoc} */
    public void registerConsumer(EventEnum<?> type, EventConsumer consumer) {
        typeToConsumerMap.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        consumerToTypeMap.computeIfAbsent(consumer, k -> new CopyOnWriteArrayList<>());

        if (typeToConsumerMap.get(type).contains(consumer)) {
            return;
        }

        typeToConsumerMap.get(type).add(consumer);
        consumerToTypeMap.get(consumer).add(type);
    }

    /** {@inheritDoc} */
    @Override
    public void unregisterConsumer(EventConsumer consumer) {
        List<EventEnum<? extends Enum<?>>> types = consumerToTypeMap.remove(consumer);
        if (types == null || types.isEmpty()) {
            return;
        }
        for (EventEnum<? extends Enum<?>> type : types) {
            typeToConsumerMap.computeIfPresent(type, (k, list) -> {
                list.remove(consumer);
                return list.isEmpty() ? null : list;
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unregisterConsumer(EventEnum<?> type, EventConsumer consumer) {
        typeToConsumerMap.computeIfPresent(type, (k, list) -> {
            list.remove(consumer);
            return list.isEmpty() ? null : list;
        });
        consumerToTypeMap.computeIfPresent(consumer, (k, list) -> {
            list.remove(type);
            return list.isEmpty() ? null : list;
        });
    }

    /** {@inheritDoc} */
    public void enqueue(Event event) {
        if (!process) {
            log.debug("Default Event Processor has shutdown! Event will not be processed.");
            return;
        }

        eventQueue.offer(event);
        synchronized (lock) {
            lock.notify();
        }
    }

    private static class ProcessorThread extends Thread {

        private final ExecutorService executorService = Executors.newCachedThreadPool();

        public ProcessorThread() {
            super("Event-Processing-Thread");
        }

        @Override
        public void run() {
            processEvents();
        }

        private void processEvents() {
            while (process) {
                Event event = eventQueue.poll();
                if (event == null) {
                    block();
                } else {
                    notifyConsumers(event);
                }
            }
            executorService.shutdown();
        }

        private void block() {
            synchronized (lock) {
                while (eventQueue.isEmpty() && process) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        private void notifyConsumers(Event event) {
            List<EventConsumer> eventConsumers = typeToConsumerMap.get(event.getType());

            if (eventConsumers == null) {
                return;
            }

            for (EventConsumer consumer: eventConsumers) {
                executorService.submit(() -> consumer.consume(event));
            }
        }
    }
}
