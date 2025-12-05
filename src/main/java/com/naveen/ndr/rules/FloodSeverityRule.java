package com.naveen.ndr.rules;

import com.naveen.ndr.model.DisasterEvent;

/**
 * FloodSeverityRule
 * Stateless implementation of SeverityRule for flood events. It reads flood-related
 * inputs from DisasterEvent and computes a flood-specific severity score in the range 0-100.
 */
public class FloodSeverityRule implements SeverityRule {

    // Default weights (sum ~1.0)
    private static final double WP = 0.40; // population weight
    private static final double WI = 0.20; // infrastructure weight
    private static final double WA = 0.15; // accessibility weight
    private static final double WT = 0.15; // time-sensitivity weight
    private static final double WC = 0.10; // cascading risk weight

    @Override
    public int calculateSeverity(DisasterEvent event) {
        if (event == null) throw new IllegalArgumentException("event cannot be null");

        // Read inputs with safe defaults
        int populationAffected = Math.max(0, event.getPopulationAffected());
        int infraDamage = clampPercent(event.getInfraDamageLevel());
        int accessibility = clampPercent(event.getAccessibilityLevel());
        double spreadRate = Math.max(0.0, event.getSpreadRate());
        int cascading = clampPercent(event.getCascadingRiskLevel());

        // P_index: population exposure (0-100)
        int pIndex = computePopulationIndex(event, populationAffected);

        // I_index: infrastructure damage (use the infraDamage directly)
        int iIndex = infraDamage;

        // A_index: accessibility (0-100)
        int aIndex = accessibility;

        // T_index: map spreadRate to urgency (0-100)
        int tIndex = mapSpreadRateToIndex(spreadRate);

        // C_index: cascading risk (0-100)
        int cIndex = cascading;

        // Combine with weights
        double rawScore = WP * pIndex + WI * iIndex + WA * aIndex + WT * tIndex + WC * cIndex;

        // Bonuses / multipliers
        double bonus = 0.0;

        // Example: if spread rate is extremely high, boost
        if (spreadRate > 5.0) bonus += 10.0;

        // If large population exposed and accessibility poor, add a trapped-people bonus
        if (pIndex >= 70 && aIndex >= 50) bonus += 10.0;

        // If cascading risk high (e.g., dam threat), add a strong bonus
        if (cIndex >= 70) bonus += 15.0;

        // Final score & clamp to 0-100
        int finalScore = clampPercent((int) Math.round(rawScore + bonus));
        return finalScore;
    }

    // Helper: compute population index based on available data
    private int computePopulationIndex(DisasterEvent event, int populationAffected) {
        // If we had state total population available, we could compute ratio.
        // For now, use bucket mapping if total population is not present in event.

        // Try to infer from event severity fields: if populationAffected is zero, return 0
        if (populationAffected <= 0) return 0;

        // Bucket mapping (tunable): small -> low, medium -> medium, large -> high
        if (populationAffected <= 100) return 10;
        if (populationAffected <= 10dspulationAffected <= 10000) return 60;
        if (populationAffected <= 50000) return 80;
        return 95; // very large affected
    }

    // Helper: map spread rate (units arbitrary but consistent) to urgency index
    private int mapSpreadRateToIndex(double spreadRate) {
        if (spreadRate <= 0.5) return 10;
        if (spreadRate <= 2.0) return 30;
        if (spreadRate <= 5.0) return 60;
        return 90; // extremely fast
    }

    // Clamp integer to 0..100
    private int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}