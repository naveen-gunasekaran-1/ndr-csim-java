package com.naveen.ndr.engine;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.model.ResourcePool;
import com.naveen.ndr.states.StateNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * NationalCoordinator
 *
 * Central orchestrator: consumes raw events from producer queue, uses SeverityCalculator to compute
 * event severity, enqueues into the dispatch priority queue, and manages lifecycle of StateNodes and Dispatcher.
 */
public class NationalCoordinator implements Runnable {

    private final BlockingQueue<DisasterEvent> producerQueue;
    private final PriorityBlockingQueue<DisasterEvent> dispatchQueue;
    private final SeverityCalculator severityCalculator;
    private final Dispatcher dispatcher;
    private final ResourcePool resourcePool;
    private final List<StateNode> stateNodes = new ArrayList<>();

    private volatile boolean running;
    private Thread thread;

    public NationalCoordinator(BlockingQueue<DisasterEvent> producerQueue,
                               PriorityBlockingQueue<DisasterEvent> dispatchQueue,
                               SeverityCalculator severityCalculator,
                               Dispatcher dispatcher,
                               ResourcePool resourcePool) {
        if (producerQueue == null) throw new IllegalArgumentException("producerQueue required");
        if (dispatchQueue == null) throw new IllegalArgumentException("dispatchQueue required");
        if (severityCalculator == null) throw new IllegalArgumentException("severityCalculator required");
        if (dispatcher == null) throw new IllegalArgumentException("dispatcher required");
        if (resourcePool == null) throw new IllegalArgumentException("resourcePool required");

        this.producerQueue = producerQueue;
        this.dispatchQueue = dispatchQueue;
        this.severityCalculator = severityCalculator;
        this.dispatcher = dispatcher;
        this.resourcePool = resourcePool;
    }

    public synchronized void registerStateNode(StateNode node) {
        if (node == null) throw new IllegalArgumentException("node required");
        stateNodes.add(node);
    }

    public synchronized void start() {
        if (running) return;
        running = true;

        // start all StateNodes
        for (StateNode n : stateNodes) n.start();

        // start dispatcher
        dispatcher.start();

        // start coordinator runtime
        thread = new Thread(this, "NationalCoordinator");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        // stop all state nodes
        for (StateNode n : stateNodes) n.stop();
        // stop dispatcher
        dispatcher.stop();
        if (thread != null) thread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for new event from producers
                DisasterEvent event = producerQueue.poll(1, TimeUnit.SECONDS);
                if (event == null) continue;

                // Compute severity
                int severity = severityCalculator.calculateSeverity(event);
                event.setSeverityScore(severity);

                // Enqueue into dispatch queue
                dispatchQueue.put(event);

                System.out.println("[Coordinator] Event queued for dispatch: " + event.getEventId() + " severity=" + severity + " " + event.getEventType());

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        running = false;
    }
}
