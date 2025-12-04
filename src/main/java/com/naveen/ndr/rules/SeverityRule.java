package com.naveen.ndr.rules;

import com.naveen.ndr.model.DisasterEvent;

/**
 * SeverityRule
 * Interface for disaster-specific severity rules. Each implementation evaluates a
 * DisasterEvent and returns an integer severity contribution (0-100) or a component
 * used by the SeverityCalculator. Implementations should be stateless and
 * thread-safe.
 */
public interface SeverityRule {

    /**
     * Calculate a severity score (0-100) for the provided event according to this rule's logic.
     * Implementations should not mutate the event. The returned value represents this rule's
     * contribution or the final severity for a type-specific rule.
     * @param event DisasterEvent to evaluate
     * @return integer severity (0-100)
     */
    int calculateSeverity(DisasterEvent event);
    /**
     * Optional short name for the rule (useful for reporting/debugging).
     */
    default String name() {
        return this.getClass().getSimpleName();
    }
}
