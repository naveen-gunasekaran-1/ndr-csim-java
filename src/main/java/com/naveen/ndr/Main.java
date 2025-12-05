package com.naveen.ndr;

import com.naveen.ndr.engine.*;
import com.naveen.ndr.model.*;
import com.naveen.ndr.reporting.Reporter;
import com.naveen.ndr.rules.*;
import com.naveen.ndr.states.StateNode;
import com.naveen.ndr.util.ConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main - National Disaster Response Computer Simulation
 * 
 * Complete simulation of India's National Disaster Response Framework.
 * This simulation models:
 * - Multiple states generating disaster events based on their profiles
 * - Severity calculation using type-specific rules
 * - Priority-based resource allocation
 * - Real-time system monitoring and reporting
 * 
 * @author Naveen Gunasekaran
 * @version 2.0
 * @since December 5, 2025
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   NDR-CSIM: National Disaster Response Simulation          ║");
        System.out.println("║   Computer Simulation of India's NDRF Framework            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Load configuration
        ConfigLoader config = new ConfigLoader();
        long simulationDuration = config.getLong("simulation.duration.ms", 60000);
        long stateInterval = config.getLong("state.interval.ms", 3000);
        long reporterInterval = config.getLong("reporter.interval.ms", 5000);

        System.out.println("[Config] Simulation Duration: " + (simulationDuration / 1000) + " seconds");
        System.out.println("[Config] State Event Interval: " + (stateInterval / 1000) + " seconds");
        System.out.println("[Config] Reporter Interval: " + (reporterInterval / 1000) + " seconds\n");

        // Initialize event queues
        EventQueueManager queueManager = new EventQueueManager();
        System.out.println("[Init] Event queues initialized");

        // Initialize national resource pool
        ResourcePool resourcePool = new ResourcePool(
                config.getInt("resources.ndrf.units", 50),
                config.getInt("resources.trucks", 100),
                config.getInt("resources.boats", 40),
                config.getInt("resources.helicopters", 30),
                config.getInt("resources.medical.teams", 60)
        );
        System.out.println("[Init] Resource Pool: " + resourcePool.getSummary());

        // Initialize severity calculator with all rules
        SeverityCalculator severityCalculator = new SeverityCalculator("national-calculator");
        severityCalculator.addRule(new FloodSeverityRule());
        severityCalculator.addRule(new CycloneSeverityRule());
        severityCalculator.addRule(new WildfireSeverityRule());
        severityCalculator.addRule(new LandslideSeverityRule());
        System.out.println("[Init] Severity Calculator configured with 4 rules\n");

        // Initialize dispatcher
        Dispatcher dispatcher = new Dispatcher(
                queueManager.getDispatchQueue(),
                resourcePool
        );
        System.out.println("[Init] Dispatcher initialized");

        // Initialize national coordinator
        NationalCoordinator coordinator = new NationalCoordinator(
                queueManager.getProducerQueue(),
                queueManager.getDispatchQueue(),
                severityCalculator,
                dispatcher,
                resourcePool
        );
        System.out.println("[Init] National Coordinator initialized");

        // Initialize reporter
        Reporter reporter = new Reporter(
                resourcePool,
                queueManager.getProducerQueue(),
                queueManager.getDispatchQueue(),
                reporterInterval
        );
        System.out.println("[Init] Reporter initialized\n");

        // Create state nodes with realistic Indian state profiles
        List<StateNode> stateNodes = createStateNodes(queueManager, stateInterval);
        System.out.println("[Init] Created " + stateNodes.size() + " state nodes\n");

        // Register state nodes with coordinator
        for (StateNode node : stateNodes) {
            coordinator.registerStateNode(node);
        }
        System.out.println("[Init] All state nodes registered with coordinator\n");

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              STARTING SIMULATION                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Start all components
        coordinator.start();
        reporter.start();

        System.out.println("[Runtime] All systems operational\n");
        System.out.println("════════════════════════════════════════════════════════════\n");

        // Run simulation for configured duration
        try {
            TimeUnit.MILLISECONDS.sleep(simulationDuration);
        } catch (InterruptedException e) {
            System.err.println("[Runtime] Simulation interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Shutdown sequence
        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.println("\n[Shutdown] Initiating graceful shutdown...\n");

        coordinator.stop();
        reporter.stop();

        // Give threads time to complete
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Final statistics
        printFinalStatistics(queueManager, resourcePool);

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("  ║           SIMULATION COMPLETED SUCCESSFULLY                ║");
        System.out.println("  ╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Creates state nodes representing major Indian states with realistic profiles
     */
    private static List<StateNode> createStateNodes(EventQueueManager queueManager, long baseInterval) {
        List<StateNode> nodes = new ArrayList<>();

        // Kerala - High flood risk, coastal
        State kerala = new State(
                "KL", "Kerala", 35_000_000L, 38852.0,
                State.TerrainType.COASTAL, 250, 150, 45, true,
                State.RiskProfile.HIGH, 1.5,
                List.of("FLOOD", "LANDSLIDE", "CYCLONE"),
                "kerala-ndrf@gov.in"
        );
        nodes.add(new StateNode(kerala, queueManager.getProducerQueue(), baseInterval));

        // Maharashtra - Industrial hub, plain terrain
        State maharashtra = new State(
                "MH", "Maharashtra", 125_000_000L, 307713.0,
                State.TerrainType.PLAIN, 800, 500, 200, true,
                State.RiskProfile.MEDIUM, 1.2,
                List.of("FLOOD", "INDUSTRIAL"),
                "maharashtra-ndrf@gov.in"
        );
        nodes.add(new StateNode(maharashtra, queueManager.getProducerQueue(), (long) (baseInterval * 1.2)));

        // Uttarakhand - Mountain state, high landslide risk
        State uttarakhand = new State(
                "UK", "Uttarakhand", 11_000_000L, 53483.0,
                State.TerrainType.MOUNTAIN, 120, 80, 20, true,
                State.RiskProfile.HIGH, 1.3,
                List.of("LANDSLIDE", "FLOOD", "EARTHQUAKE"),
                "uttarakhand-ndrf@gov.in"
        );
        nodes.add(new StateNode(uttarakhand, queueManager.getProducerQueue(), (long) (baseInterval * 0.9)));

        // Rajasthan - Low risk desert state
        State rajasthan = new State(
                "RJ", "Rajasthan", 80_000_000L, 342239.0,
                State.TerrainType.PLAIN, 400, 300, 100, false,
                State.RiskProfile.LOW, 0.7,
                List.of("INDUSTRIAL", "WILDFIRE"),
                "rajasthan-ndrf@gov.in"
        );
        nodes.add(new StateNode(rajasthan, queueManager.getProducerQueue(), (long) (baseInterval * 1.5)));

        // Assam - High flood risk, plain terrain
        State assam = new State(
                "AS", "Assam", 32_000_000L, 78438.0,
                State.TerrainType.PLAIN, 180, 120, 50, false,
                State.RiskProfile.HIGH, 1.4,
                List.of("FLOOD", "EARTHQUAKE"),
                "assam-ndrf@gov.in"
        );
        nodes.add(new StateNode(assam, queueManager.getProducerQueue(), baseInterval));

        // Odisha - Cyclone prone coastal state
        State odisha = new State(
                "OD", "Odisha", 45_000_000L, 155707.0,
                State.TerrainType.COASTAL, 300, 200, 80, false,
                State.RiskProfile.HIGH, 1.6,
                List.of("CYCLONE", "FLOOD"),
                "odisha-ndrf@gov.in"
        );
        nodes.add(new StateNode(odisha, queueManager.getProducerQueue(), (long) (baseInterval * 0.8)));

        return nodes;
    }

    /**
     * Prints final simulation statistics
     */
    private static void printFinalStatistics(EventQueueManager queueManager, ResourcePool resourcePool) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              FINAL SIMULATION STATISTICS                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Queue Status:");
        System.out.println("  Producer Queue: " + queueManager.getProducerQueue().size() + " pending");
        System.out.println("  Dispatch Queue: " + queueManager.getDispatchQueue().size() + " pending");
        System.out.println();
        System.out.println("Resource Status:");
        System.out.println("  " + resourcePool.getSummary());
        System.out.println();
    }
}
