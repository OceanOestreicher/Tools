package custom.striker.gui;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Alternative Timer for use in JSwing applications. This timer makes use of a single Daemon thread so
 * as not to block the JVM from exiting. Each timer exists on its own thread.
 */
public class Timer {
    private final ScheduledExecutorService exec;
    private final Runnable task;
    private final int delayMs;

    private ScheduledFuture<?> future;

    public Timer(int delayMs, Runnable task) {
        this.delayMs = delayMs;
        this.exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Swing-like-Timer");
            t.setDaemon(true);
            return t;
        });
        this.task = () -> {
            if (!exec.isShutdown()) {
                SwingUtilities.invokeLater(task);
            }
        };

    }

    /**
     * Starts or restarts the timer.
     */
    public synchronized void restart() {
        // Cancel previous scheduled execution (if any)
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }

        // Schedule a new execution
        future = exec.schedule(task, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the timer without firing its task
     */
    public synchronized void stop() {
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * Shuts down the Timer's executor service. This method must be run
     * to ensure the JVM exits successfully.
     */
    public synchronized void shutdown() {
        exec.shutdownNow();
    }
}
