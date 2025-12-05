package com.naveen.ndr.engine;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.rules.SeverityRule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SeverityCalculator
 * Responsible for combining multiple SeverityRule implementations to compute a final
 * severity score (0-100) for a DisasterEvent. Rules are applied in a pluggable fashion
 * and their numeric outputs are aggregated using configurable weights.
 * This class is thread-safe: rules may be added/removed at runtime and calculateSeverity
 * can be invoked concurrently.
 */
public class SeverityCalculator {

    // Thread-safe list so callers can add/remove rules while other threads compute severity.
    private final CopyOnWriteArrayList<SeverityRule> rules = new CopyOnWriteArrayList<>();

    // An optional name or id for diagnostics
    private final String id;

    // Simple constructor
    public SeverityCalculator(String id) {
        this.id = id;
    }

    public SeverityCalculator() {
        this("default-calculator");
    }

    /**
     * Register a rule. The same rule type may be added once; duplicates are allowed but not recommended.
     */
    public void addRule(SeverityRule rule) {
        if (rule == null) throw new IllegalArgumentException("rule cannot be null");
        rules.add(rule);
    }

    /**
     * Remove a rule instance.
     */
    public void removeRule(SeverityRule rule) {
        rules.remove(rule);
    }

    /**
     * Clear all rules.
     */
    public void clearRules() {
        rules.clear();
    }

    /**
     * Compute the final severity for the given event by averaging rule outputs.
     * Each rule returns an integer 0-100. The final value is the rounded average.
     * The result is stored into the event via setSeverityScore.
     * Note: A production-grade system might apply weighted aggregation or normalization;
     * this implementation favors simplicity and deterministic behavior for the simulator.
     */
    public int calculateSeverity(DisasterEvent event) {
        if (event == null) throw new IllegalArgumentException("event cannot be null");

        List<SeverityRule> snapshot = rules;
        if (snapshot.isEmpty()) {
            event.setSeverityScore(0);
            return 0;
        }

        int sum = 0;
        for (SeverityRule r : snapshot) {
            int v = r.calculateSeverity(event);
            if (v < 0) v = 0;
            if (v > 100) v = 100;
            sum += v;
        }

        int avg = Math.round((float) sum / snapshot.size());
        // Persist into event
        event.setSeverityScore(avg);
        return avg;
    }

    public String getId() { return id; }

    @Override
    public String toString() {
        return "SeverityCalculator{" + "id='" + id + '\'' + ", rulesCount=" + rules.size() + '}';
    }
}
