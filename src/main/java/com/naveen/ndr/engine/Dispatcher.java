package com.naveen.ndr.engine;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.model.ResourcePool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Dispatcher
 * Responsible for consuming DisasterEvent instances from a priority queue (highest severity first)
 * and attempting to allocate resources from a shared ResourcePool. On successful allocation the
 * Dispatcher simulates a response (asynchronous) and releases resources back to the pool after
 * a short delay.
 * The Dispatcher is simple by design: it builds a resource request based on event type and
 * severity, attempts an atomic allocation, and if allocation fails, re-queues the event and
 * waits before retrying to avoid busy-waiting.
 */
public class Dispatcher implements Runnable {

    private final PriorityBlockingQueue<DisasterEvent> queue;
    private final ResourcePool resourcePool;
    private volatile boolean running;
    private Thread thread;

    public Dispatcher(PriorityBlockingQueue<DisasterEvent> queue, ResourcePool resourcePool) {
        if (queue == null) throw new IllegalArgumentException("queue required");
        if (resourcePool == null) throw new IllegalArgumentException("resourcePool required");
        this.queue = queue;
        this.resourcePool = resourcePool;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "Dispatcher");
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
                // Take highest-priority event (blocks until available)
                DisasterEvent event = queue.take();

                if (event == null) continue;

                // If severity is not yet computed, place back and wait
                if (event.getSeverityScore() <= 0) {
                    // push back and yield
                    queue.put(event);
                    TimeUnit.MILLISECONDS.sleep(200);
                    continue;
                }

                Map<String, Integer> request = buildRequestForEvent(event);
                boolean allocated = resourcePool.AllocateResources(request);

                if (allocated) {
                    System.out.println("[Dispatcher] Allocated resources for event=" + event.getEventId() + " sev=" + event.getSeverityScore() + " request=" + request);

                    // Simulate response in background and release resources afterwards
                    simulateResponseAndRelease(event, request);
                } else {
                    // Allocation failed — requeue with delay to avoid tight loops
                    System.out.println("[Dispatcher] Resources unavailable for event=" + event.getEventId() + " sev=" + event.getSeverityScore() + " - requeueing");
                    queue.put(event);
                    TimeUnit.MILLISECONDS.sleep(500);
                }

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        running = false;
    }

    /**
     * Build a simple resource request map based on event type and severity.
     * Higher severity requests more resources. The numbers here are heuristic and
     * intentionally simple — adjust for realism later.
     */
    private Map<String, Integer> buildRequestForEvent(DisasterEvent event) {
        int sev = Math.max(0, event.getSeverityScore());
        Map<String, Integer> req = new HashMap<>();

        // Base multipliers
        int helis = 0; // helicopters
        int ndrf = 0; // ndrf units
        int medics = 0; // medical teams
        int boats = 0; // boats
        int trucks = 0; // trucks

        switch (event.getEventType()) {
            case FLOOD:
                boats = sev >= 70 ? 4 : sev >= 40 ? 2 : 1;
                ndrf = sev >= 70 ? 5 : sev >= 40 ? 3 : 1;
                medics = sev >= 60 ? 3 : 1;
                break;
            case CYCLONE:
                helis = sev >= 70 ? 3 : sev >= 40 ? 2 : 1;
                ndrf = sev >= 50 ? 4 : 2;
                medics = sev >= 50 ? 3 : 1;
                trucks = sev >= 30 ? 3 : 1;
                break;
            case WILDFIRE:
                helis = sev >= 80 ? 4 : sev >= 50 ? 2 : 1;
                ndrf = sev >= 70 ? 4 : 2;
                medics = sev >= 60 ? 2 : 1;
                break;
            case EARTHQUAKE:
                ndrf = sev >= 80 ? 6 : sev >= 50 ? 4 : 2;
                medics = sev >= 50 ? 4 : 2;
                trucks = sev >= 40 ? 4 : 2;
                break;
            case LANDSLIDE:
                ndrf = sev >= 60 ? 3 : 1;
                medics = sev >= 60 ? 2 : 1;
                trucks = sev >= 40 ? 2 : 1;
                break;
            case INDUSTRIAL:
            default:
                medics = sev >= 50 ? 3 : 1;
                ndrf = sev >= 60 ? 2 : 1;
                break;
        }

        // Add a small randomness factor to avoid deterministic ties
        if (helis > 0) helis += ThreadLocalRandom.current().nextInt(0, 2);
        if (ndrf > 0) ndrf += ThreadLocalRandom.current().nextInt(0, 2);
        if (medics > 0) medics += ThreadLocalRandom.current().nextInt(0, 2);
        if (boats > 0) boats += ThreadLocalRandom.current().nextInt(0, 2);
        if (trucks > 0) trucks += ThreadLocalRandom.current().nextInt(0, 2);

        if (helis > 0) req.put("helicopters", helis);
        if (ndrf > 0) req.put("ndrf", ndrf);
        if (medics > 0) req.put("medical_teams", medics);
        if (boats > 0) req.put("boats", boats);
        if (trucks > 0) req.put("trucks", trucks);

        return req;
    }

    /**
     * Simulate the response time asynchronously and release resources back into the pool.
     * Response duration is proportional to severity but with randomness.
     */
    private void simulateResponseAndRelease(DisasterEvent event, Map<String, Integer> allocated) {
        new Thread(() -> {
            try {
                int sev = Math.max(1, event.getSeverityScore());
                long baseMs = Math.min(20_000L, sev * 200L); // cap to 20s
                long jitter = ThreadLocalRandom.current().nextLong(0, 2000);
                TimeUnit.MILLISECONDS.sleep(baseMs + jitter);

                // Release resources
                resourcePool.releaseResources(allocated);
                System.out.println("[Dispatcher] Completed response for event=" + event.getEventId() + " released=" + allocated);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Responder-" + event.getEventId()).start();
    }
}
