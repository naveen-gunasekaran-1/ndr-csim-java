package com.naveen.ndr.states;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.model.EventType;
import com.naveen.ndr.model.State;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class StateNode implements Runnable {

    private final State state;
    private final BlockingQueue<DisasterEvent> outputQueue;
    private final long baseIntervalMs; // nominal interval between events
    private volatile boolean running;
    private Thread thread;

    public StateNode(State state, BlockingQueue<DisasterEvent> outputQueue, long baseIntervalMs) {
        if (state == null) throw new IllegalArgumentException("state required");
        if (outputQueue == null) throw new IllegalArgumentException("outputQueue required");
        this.state = state;
        this.outputQueue = outputQueue;
        this.baseIntervalMs = Math.max(100L, baseIntervalMs);
    }

    /**
     * Start the node in a new background thread.
     */
    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "StateNode-" + state.getStateId());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Request shutdown; the run loop will exit shortly.
     */
    public synchronized void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                // jitter so events are not perfectly periodic
                long jitter = ThreadLocalRandom.current().nextLong(-baseIntervalMs / 2, baseIntervalMs / 2 + 1);
                long wait = Math.max(200L, baseIntervalMs + jitter);
                TimeUnit.MILLISECONDS.sleep(wait);

                DisasterEvent event = generateEvent();

                // Put into queue (blocks if full)
                outputQueue.put(event);

                // Optionally log (replace with proper logger in real project)
                System.out.println("[" + Thread.currentThread().getName() + "] Generated: " + event.getEventId() + " type=" + event.getEventType() + " state=" + event.getOriginState());

            } catch (InterruptedException ie) {
                // preserve interrupt status and exit if requested
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                // Guard against unexpected exceptions and keep running
                ex.printStackTrace();
            }
        }

        running = false;
    }

    /**
     * Create a simulated DisasterEvent using the state's profile + randomness.
     */
    private DisasterEvent generateEvent() {
        // Choose event type using simple heuristics based on terrain and risk profile
        EventType type = pickEventType();

        // Unique event id
        String id = state.getStateId() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // populationAffected: fraction of state population biased by event and risk
        long totalPop = state.getTotalPopulation();
        double baseFraction = ThreadLocalRandom.current().nextDouble(0.0001, 0.02); // 0.01% - 2% by default
        // Increase chance if baseline risk HIGH
        if (state.getBaselineRiskProfile() != null && state.getBaselineRiskProfile().name().equals("HIGH")) {
            baseFraction *= 3.0;
        }
        int populationAffected = (int) Math.min(totalPop, Math.max(0, (long) (totalPop * baseFraction)));

        // infraDamageLevel: random influenced by event type
        int infraDamage = ThreadLocalRandom.current().nextInt(0, 61); // 0-60 default
        if (type == EventType.CYCLONE) infraDamage = ThreadLocalRandom.current().nextInt(20, 91);
        if (type == EventType.EARTHQUAKE) infraDamage = ThreadLocalRandom.current().nextInt(30, 101);

        // accessibilityLevel: based on terrain + random
        int terrainFactor = terrainAccessibilityFactor(state.getTerrainType());
        int accessibility = clampPercent(terrainFactor + ThreadLocalRandom.current().nextInt(-10, 31));

        // spreadRate: units vary by type; choose ranges appropriate to type
        double spreadRate;
        switch (type) {
            case WILDFIRE:
                spreadRate = ThreadLocalRandom.current().nextDouble(0.5, 60.0); // ha/hour
                break;
            case FLOOD:
                spreadRate = ThreadLocalRandom.current().nextDouble(0.1, 6.0); // m/hour or cm/hour scaled
                break;
            case CYCLONE:
                spreadRate = ThreadLocalRandom.current().nextDouble(30.0, 150.0); // wind km/h used as proxy
                break;
            case EARTHQUAKE:
                spreadRate = ThreadLocalRandom.current().nextDouble(0.0, 1.0); // aftershock risk proxy
                break;
            case LANDSLIDE:
                spreadRate = ThreadLocalRandom.current().nextDouble(0.1, 3.0);
                break;
            case INDUSTRIAL:
            default:
                spreadRate = ThreadLocalRandom.current().nextDouble(0.0, 5.0);
                break;
        }

        // cascadingRiskLevel: influenced by presence of major dam or high infra damage
        int cascading = clampPercent((state.hasMajorDam() ? 30 : 0) + (infraDamage / 2) + ThreadLocalRandom.current().nextInt(0, 21));

        long ts = System.currentTimeMillis();

        return new DisasterEvent(id, type, state.getStateName(), populationAffected, infraDamage, accessibility, spreadRate, cascading, ts);
    }

    private EventType pickEventType() {
        // Simple heuristic: coastal->CYCLONE/FLOOD, forest->WILDFIRE, hilly->LANDSLIDE, else random
        switch (state.getTerrainType()) {
            case COASTAL:
                return ThreadLocalRandom.current().nextBoolean() ? EventType.CYCLONE : EventType.FLOOD;
            case FOREST:
                return ThreadLocalRandom.current().nextDouble() < 0.7 ? EventType.WILDFIRE : EventType.FLOOD;
            case HILLY:
            case MOUNTAIN:
                return ThreadLocalRandom.current().nextDouble() < 0.6 ? EventType.LANDSLIDE : EventType.FLOOD;
            case PLAIN:
            default:
                // plains see floods and industrial incidents more often
                double r = ThreadLocalRandom.current().nextDouble();
                if (r < 0.5) return EventType.FLOOD;
                if (r < 0.8) return EventType.INDUSTRIAL;
                return EventType.WILDFIRE;
        }
    }

    private int terrainAccessibilityFactor(State.TerrainType terrain) {
        switch (terrain) {
            case PLAIN:
                return 10;
            case FOREST:
                return 40;
            case HILLY:
                return 60;
            case MOUNTAIN:
                return 80;
            case COASTAL:
            default:
                return 20;
        }
    }

    private int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}
