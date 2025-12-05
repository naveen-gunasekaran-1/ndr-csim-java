package com.naveen.ndr.util;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.model.SeverityFactors;

/**
 * SeverityInputExtractor
 * Utility to extract a SeverityFactor (P,I,A,T,C) from a DisasterEvent.
 * Centralizes the mapping between raw event fields and the SeverityFactor
 * used by rules. This keeps rules decoupled from the raw event model and
 * provides a single place to add normalization logic later.
 */
public final class SeverityInputExtractor {

    private SeverityInputExtractor() {}

    /**
     * Extract a SeverityFactor from the provided DisasterEvent.
     * Uses safe defaults and clamps values where appropriate.
     * @param event DisasterEvent (must not be null)
     * @return SeverityFactor immutable container for P,I,A,T,C
     * @throws IllegalArgumentException if event is null
     */
    public static SeverityFactors fromEvent(DisasterEvent event) {
        if (event == null) throw new IllegalArgumentException("event cannot be null");

        int p = Math.max(0, event.getPopulationAffected());
        int i = clampPercent(event.getInfraDamageLevel());
        int a = clampPercent(event.getAccessibilityLevel());
        double t = Math.max(0.0, event.getSpreadRate());
        int c = clampPercent(event.getCascadingRiskLevel());

        return new SeverityFactors(p, i, a, t, c);
    }

    /**
     * Simple helper to convert possibly-out-of-range ints to 0..100.
     */
    private static int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}