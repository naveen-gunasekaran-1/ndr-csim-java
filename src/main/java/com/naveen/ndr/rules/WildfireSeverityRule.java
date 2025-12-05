package com.naveen.ndr.rules;

import com.naveen.ndr.model.DisasterEvent;

/**
 * WildfireSeverityRule
 * Stateless implementation of SeverityRule for wildfire events. It reads wildfire-related
 * inputs from DisasterEvent and computes a wildfire-specific severity score in the range 0-100.
 * Notes:
 * - Uses spreadRate as the primary time/speed indicator (ha/hour or similar unit).
 * - Treats smoke/air-quality and proximity to populated areas via cascadingRiskLevel and populationAffected.
 * - Stateless and thread-safe.
 */
public class WildfireSeverityRule implements SeverityRule {

    // Default weights tuned for wildfires
    private static final double WP = 0.30; // population weight (exposure to fire/smoke)
    private static final double WI = 0.15; // infrastructure weight (buildings, power lines)
    private static final double WA = 0.10; // accessibility weight (terrain access difficulty)
    private static final double WT = 0.30; // time-sensitivity weight (spread rate very important)
    private static final double WC = 0.15; // cascading risk (air quality, powerline fire risk)

    @Override
    public int calculateSeverity(DisasterEvent event) {
        if (event == null) throw new IllegalArgumentException("event cannot be null");

        // Read inputs safely
        int populationAffected = Math.max(0, event.getPopulationAffected());
        int infraDamage = clampPercent(event.getInfraDamageLevel());
        int accessibility = clampPercent(event.getAccessibilityLevel());
        double spreadRate = Math.max(0.0, event.getSpreadRate());
        int cascading = clampPercent(event.getCascadingRiskLevel());

        // Compute component indices
        int pIndex = computePopulationIndex(populationAffected);
        int iIndex = infraDamage;
        int aIndex = accessibility;
        int tIndex = mapWildfireSpreadToIndex(spreadRate);
        int cIndex = cascading;

        // Weighted combination
        double raw = WP * pIndex + WI * iIndex + WA * aIndex + WT * tIndex + WC * cIndex;

        // Bonuses specific to wildfires
        double bonus = 0.0;

        // Very fast-spreading wildfire
        if (spreadRate > 50.0) bonus += 20.0;   // extreme spread (ha/hour)
        else if (spreadRate > 20.0) bonus += 10.0; // fast

        // If many people affected and poor access -> trapped / evacuation difficulty
        if (pIndex >= 60 && aIndex >= 50) bonus += 10.0;

        // If cascading risk high (very poor air quality or powerline fire risk)
        if (cIndex >= 70) bonus += 15.0;

        int finalScore = clampPercent((int) Math.round(raw + bonus));
        return finalScore;
    }

    // Population buckets suitable for wildfire exposure
    private int computePopulationIndex(int populationAffected) {
        if (populationAffected <= 0) return 0;
        if (populationAffected <= 50) return 10;
        if (populationAffected <= 500) return 30;
        if (populationAffected <= 5000) return 60;
        if (populationAffected <= 20000) return 80;
        return 95;
    }

    // Map wildfire spread rate (ha/hour or similar) to urgency
    private int mapWildfireSpreadToIndex(double spreadRate) {
        if (spreadRate <= 1.0) return 10;    // very slow
        if (spreadRate <= 5.0) return 30;    // slow
        if (spreadRate <= 20.0) return 60;   // moderate
        if (spreadRate <= 50.0) return 85;   // fast
        return 95; // extreme
    }

    // Clamp helper
    private int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}