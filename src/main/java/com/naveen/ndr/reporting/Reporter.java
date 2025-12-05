package com.naveen.ndr.reporting;

import com.naveen.ndr.model.ResourcePool;
import com.naveen.ndr.model.DisasterEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Reporter
 *
 * A lightweight monitoring component that runs in a background thread and periodically
 * prints system status:
 *   - resource availability
 *   - queue sizes
 *   - top pending events in the dispatch queue
 *
 * Helps visualize the simulation in real-time.
 */
public class Reporter implements Runnable {

    private final ResourcePool resourcePool;
    private final BlockingQueue<DisasterEvent> producerQueue;
    private final PriorityBlockingQueue<DisasterEvent> dispatchQueue;
    private final long intervalMs;

    private volatile boolean running;
    private Thread thread;

    public Reporter(ResourcePool resourcePool,
                    BlockingQueue<DisasterEvent> producerQueue,
                    PriorityBlockingQueue<DisasterEvent> dispatchQueue,
                    long intervalMs) {
        if (resourcePool == null) throw new IllegalArgumentException("resourcePool required");
        if (producerQueue == null) throw new IllegalArgumentException("producerQueue required");
        if (dispatchQueue == null) throw new IllegalArgumentException("dispatchQueue required");

        this.resourcePool = resourcePool;
        this.producerQueue = producerQueue;
        this.dispatchQueue = dispatchQueue;
        this.intervalMs = Math.max(500L, intervalMs);
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "Reporter");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                TimeUnit.MILLISECONDS.sleep(intervalMs);
                printStatus();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Prints a snapshot of resource availability and queue state.
     */
    private void printStatus() {
        System.out.println("\n===== SYSTEM STATUS =====");

        // Resource snapshot
        System.out.println(resourcePool.getSummary());

        // Queue sizes
        System.out.println("Producer Queue Size: " + producerQueue.size());
        System.out.println("Dispatch Queue Size: " + dispatchQueue.size());

        // Show top 3 events waiting for dispatch
        System.out.println("Top Pending Events:");
        dispatchQueue.stream()
                .sorted((a, b) -> Integer.compare(b.getSeverityScore(), a.getSeverityScore()))
                .limit(3)
                .forEach(e -> System.out.println("  → " + e.getEventId() + " | Sev=" + e.getSeverityScore() + " | " + e.getEventType()));

        System.out.println("========================\n");
    }
}