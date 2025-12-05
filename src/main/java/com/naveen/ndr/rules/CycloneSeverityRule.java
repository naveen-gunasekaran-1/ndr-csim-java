package com.naveen.ndr.rules;

import com.naveen.ndr.model.DisasterEvent;

public class CycloneSeverityRule implements SeverityRule {

    private static final double WP = 0.40; //population weights
    private static final double WI = 0.20; //infrastructure weights
    private static final double WA = 0.15; //Accessibility weights
    private static final double WT = 0.15; //Time-sensitivity weights
    private static final double WC = 0.15; //cascading weight risk

    @Override
    public int calculateSeverity(DisasterEvent event) {
        if (event == null) throw new IllegalArgumentException("event cannot be null");

        int populationAffected = Math.max(0, event.getPopulationAffected());
        int infraDamage = clampPercent(event.getInfraDamageLevel());
        int accessibility = clampPercent(event.getAccessibilityLevel());
        double spreadRate = Math.max(0.0, event.getSpreadRate());
        int cascading = clampPercent(event.getCascadingRiskLevel());

        int pIndex = computePopulation(event, populationAffected);
        int iIndex = infraDamage;
        int aIndex = accessibility;
        int tIndex = mapSpreadRateToIndex(spreadRate);
        int cIndex = cascading;

        double rawScore = WP * pIndex + WI * iIndex + WA * aIndex + WT * tIndex + WC * cIndex;
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

    public int computePopulation(DisasterEvent Event, int populationAffected) {
        if (populationAffected <= 0) return 0;

        // Bucket mapping (tunable): small -> low, medium -> medium, large -> high
        if (populationAffected <= 100) return 10;
        if (populationAffected <= 1000) return 30;
        if (populationAffected <= 10000) return 60;
        if (populationAffected <= 50000) return 80;
        return 95; // very large affected
    }
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